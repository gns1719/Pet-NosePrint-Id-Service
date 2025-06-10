import 'dart:io';
import 'dart:math';
import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:nose_stamp/config/api_config.dart'; // 너의 API 엔드포인트 정의된 곳
import 'package:nose_stamp/services/auth_service.dart'; // accessToken 불러오는 곳



class NoseStampRegisterPage extends StatefulWidget {
  final String petId;
  const NoseStampRegisterPage({super.key, required this.petId});
  

  @override
  State<NoseStampRegisterPage> createState() => _NoseStampRegisterPageState();
}

class _NoseStampRegisterPageState extends State<NoseStampRegisterPage> {
  late CameraController _cameraController;
  bool _isInitialized = false;
  final List<File?> _images = [null, null, null]; // 0: front, 1: below, 2: above
  int _currentIndex = 0;
  bool _isLoading = false;

  final List<String> _titles = ['정면', '아래', '위'];

  @override
  void initState() {
    super.initState();
    _initCamera();
  }

  Future<void> _initCamera() async {
    final cameras = await availableCameras();
    final back = cameras.firstWhere((c) => c.lensDirection == CameraLensDirection.back);
    _cameraController = CameraController(back, ResolutionPreset.medium, enableAudio: false);
    await _cameraController.initialize();
    if (mounted) setState(() => _isInitialized = true);
  }

  @override
  void dispose() {
    _cameraController.dispose();
    super.dispose();
  }

  Future<void> _captureFlow() async {
    try {
      final XFile file = await _cameraController.takePicture();
      setState(() {
        _images[_currentIndex] = File(file.path);
      });
    } catch (e) {
      debugPrint("촬영 실패: $e");
    }
  }

  void _retakePicture() {
    setState(() {
      _images[_currentIndex] = null;
    });
  }

  Future<void> _submit() async {
  if (_images.any((img) => img == null)) return;
  setState(() => _isLoading = true);

  try {

    final tokens = await AuthService().getTokens();
      final accessToken = tokens['accessToken'];

      if (accessToken == null) {
        throw Exception('로그인이 필요합니다');
      }
    final petId = widget.petId.toString();

    for (int i = 0; i < _images.length; i++) {
      final image = _images[i];
      final title = _titles[i]; // '정면', '아래', '위'

      final fileName = '$petId-$title';
      final presignedUrlResponse = await http.get(
        Uri.parse(ApiConfig.nosePresignedUrl(fileName)),
        headers: {
          'Authorization': 'Bearer $accessToken',
        },
      );

      if (presignedUrlResponse.statusCode < 200 || presignedUrlResponse.statusCode >= 300) {
        throw Exception('[$title] presigned URL 요청 실패: ${presignedUrlResponse.statusCode}');
      }

      final presignedUrl = presignedUrlResponse.body;
      final imageBytes = await image!.readAsBytes();

      final uploadResponse = await http.put(
        Uri.parse(presignedUrl),
        headers: {
          'Content-Type': 'image/jpeg',
        },
        body: imageBytes,
      );

      if (uploadResponse.statusCode < 200 || uploadResponse.statusCode >= 300) {
        throw Exception('[$title] 이미지 업로드 실패: ${uploadResponse.statusCode}');
      }
    }


    // 2. 모델 분석 요청 (업로드 성공 이후)
    final analysisResponse = await http.post(
      Uri.parse(ApiConfig.noseSaveCheck(petId)),
      headers: {
        'Authorization': 'Bearer $accessToken',
        'Content-Type': 'application/json',
      },
    );

    if (analysisResponse.statusCode < 200 || analysisResponse.statusCode >= 300) {
      throw Exception('분석 요청 실패: ${analysisResponse.statusCode}');
    }

    // 분석 결과 자체는 무시하고 완료 메시지만 표시
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('✅ 비문 등록 및 분석이 완료되었습니다')),
      );
      Navigator.of(context).pop(true);
    }
  } catch (e) {
    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('🚨 등록 실패: $e')),
      );
    }
  } finally {
    if (mounted) setState(() => _isLoading = false);
  }
}


  Widget _buildCameraBackground() {
    final size = MediaQuery.of(context).size;
    final scale = 1 / (_cameraController.value.aspectRatio * size.aspectRatio);
    return Transform.scale(
      scale: scale,
      child: Center(child: CameraPreview(_cameraController)),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('비문 등록'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => Navigator.pop(context),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.send),
            onPressed: (_images.every((img) => img != null) && !_isLoading) ? _submit : null,
          )
        ],
        bottom: PreferredSize(
          preferredSize: const Size.fromHeight(48),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: List.generate(3, (index) {
              return TextButton(
                onPressed: () => setState(() => _currentIndex = index),
                child: Text(
                  _titles[index],
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    color: _currentIndex == index ? const Color(0xFFB88C65) : Colors.grey,
                  ),
                ),
              );
            }),
          ),
        ),
      ),
      body: !_isInitialized
          ? const Center(child: CircularProgressIndicator())
          : Stack(
              children: [
                _buildCameraBackground(),
                if (_images[_currentIndex] == null)
                  Center(
                    child: SizedBox(
                      width: 280,
                      height: 280,
                      child: CustomPaint(painter: DottedCirclePainter()),
                    ),
                  ),
                if (_images[_currentIndex] != null)
                  Positioned.fill(
                    child: Image.file(
                      _images[_currentIndex]!,
                      fit: BoxFit.cover,
                    ),
                  ),
                Positioned(
                  bottom: 120,
                  left: 0,
                  right: 0,
                  child: Center(
                    child: Text(
                      _images[_currentIndex] == null
                          ? '강아지 코를 ${_titles[_currentIndex]}에서 찍어주세요'
                          : '선명하게 찍으면 정확도가 올라갑니다',
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 16,
                        shadows: [
                          Shadow(color: Colors.black, blurRadius: 3, offset: Offset(1, 1))
                        ],
                      ),
                    ),
                  ),
                ),
                Positioned(
                  bottom: 60,
                  left: 0,
                  right: 0,
                  child: Center(
                    child: ElevatedButton.icon(
                      onPressed: _images[_currentIndex] == null ? _captureFlow : _retakePicture,
                      icon: const Icon(Icons.camera_alt),
                      label: Text(_images[_currentIndex] == null ? '촬영하기' : '다시 촬영'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFFB88C65),
                        foregroundColor: Colors.white,
                        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
                      ),
                    ),
                  ),
                ),
                if (_isLoading)
                  Container(
                    color: Colors.black45,
                    child: const Center(child: CircularProgressIndicator()),
                  ),
              ],
            ),
    );
  }
}

class DottedCirclePainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    const dotRadius = 2.0;
    const dotCount = 100;
    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.width / 2;

    for (int i = 0; i < dotCount; i++) {
      final angle = (2 * pi * i) / dotCount;
      final x = center.dx + radius * cos(angle);
      final y = center.dy + radius * sin(angle);
      canvas.drawCircle(Offset(x, y), dotRadius, Paint()..color = Colors.white70);
    }
  }

  @override
  bool shouldRepaint(CustomPainter oldDelegate) => false;
}
