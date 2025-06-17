import 'dart:io';
import 'dart:math';
import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:nose_stamp/config/api_config.dart'; // 너의 API 엔드포인트 정의된 곳
import 'package:nose_stamp/services/auth_service.dart'; // accessToken 불러오는 곳
import 'dart:convert';


class NoseScanPage extends StatefulWidget {
  const NoseScanPage({super.key});

  @override
  State<NoseScanPage> createState() => _NoseScanPageState();
}

class _NoseScanPageState extends State<NoseScanPage> {
  late CameraController _cameraController;
  bool _isInitialized = false;
  XFile? previewImage;
  Map<String, dynamic>? matchResult;
  bool isLoading = false;

  @override
  void initState() {
    super.initState();
    _initializeCamera();
  }

  Future<void> _initializeCamera() async {
    final cameras = await availableCameras();
    final backCamera = cameras.firstWhere(
      (camera) => camera.lensDirection == CameraLensDirection.back,
    );

    _cameraController = CameraController(
      backCamera,
      ResolutionPreset.medium,
      enableAudio: false,
    );

    await _cameraController.initialize();
    if (mounted) {
      setState(() {
        _isInitialized = true;
      });
    }
  }

  @override
  void dispose() {
    _cameraController.dispose();
    super.dispose();
  }

  Future<void> _takePicture() async {
    try {
      final image = await _cameraController.takePicture();
      setState(() {
        previewImage = image;
      });
    } catch (e) {
      debugPrint("🚨 촬영 오류: $e");
    }
  }

  Future<void> sendToServer(XFile image) async {
  setState(() {
    isLoading = true;
  });

  try {
    final tokens = await AuthService().getTokens();
    final accessToken = tokens['accessToken'];

    if (accessToken == null) {
      throw Exception('로그인이 필요합니다');
    }

    final presignedUrlResponse = await http.get(
      Uri.parse(ApiConfig.noseCheckPresignedUrl),
      headers: {
        'Authorization': 'Bearer $accessToken',
      },
    );

    if (presignedUrlResponse.statusCode < 200 || presignedUrlResponse.statusCode >= 300) {
      throw Exception('presigned URL 요청 실패: ${presignedUrlResponse.statusCode}');
    }

    final presignedUrl = presignedUrlResponse.body;
    final imageBytes = await image.readAsBytes();

    final uploadResponse = await http.put(
      Uri.parse(presignedUrl),
      headers: {
        'Content-Type': 'image/jpeg',
      },
      body: imageBytes,
    );

    if (uploadResponse.statusCode < 200 || uploadResponse.statusCode >= 300) {
      throw Exception('이미지 업로드 실패: ${uploadResponse.statusCode}');
    }

    setState(() {
      isLoading = true;
    });


    // 3. 서버에 분석 요청 (이미지 URL은 생략)
    final analysisResponse = await http.post(
      Uri.parse(ApiConfig.noseAnalysisUrl),
      headers: {
        'Authorization': 'Bearer $accessToken',
        'Content-Type': 'application/json',
      },
    );

    if (analysisResponse.statusCode < 200 || analysisResponse.statusCode >= 300) {
      throw Exception('분석 요청 실패: ${analysisResponse.statusCode}');
    }

    final result = json.decode(analysisResponse.body);        // 결과 파싱 후 출력 만들어야함
    debugPrint("✅ 분석 결과: $result");

    setState(() {
      matchResult = result;
    });

    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('✅ 비문 분석이 완료되었습니다')),
    );
  } catch (e) {
    debugPrint("🚨 오류: $e");
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(e.toString())),
    );
  } finally {
    setState(() {
      isLoading = false;
    });
  }
}


  @override
  Widget build(BuildContext context) {
    if (!_isInitialized) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }
    if (isLoading) {
    return const Scaffold(
      backgroundColor: Colors.black,
      body: Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            CircularProgressIndicator(color: Colors.white),
            SizedBox(height: 16),
            Text(
              '🔍 비문 분석 중입니다...',
              style: TextStyle(color: Colors.white, fontSize: 18),
            ),
          ],
        ),
      ),
    );
  }

    if (matchResult != null) return _buildResultView();
    if (previewImage != null) return _buildPreviewView();
    return _buildCameraView();
  }

  Widget _buildCameraView() {
      return Scaffold(
        body: Stack(
          children: [
            _buildCameraBackground(),
            _buildCircleOverlay(),
            _buildCaptureButton(),
          ],
        ),
      );
    }

    Widget _buildCameraBackground() {
      final size = MediaQuery.of(context).size;
      final scale = 1 / (_cameraController.value.aspectRatio * size.aspectRatio);

      return Transform.scale(
        scale: scale,
        child: Center(
          child: CameraPreview(_cameraController),
        ),
      );
    }


  // 📷 미리보기 화면
  Widget _buildPreviewView() {
    return Scaffold(
      body: Stack(
        children: [
          SizedBox.expand(
            child: Image.file(
              File(previewImage!.path),
              fit: BoxFit.cover,
            ),
          ),
          Positioned(
            bottom: 120,
            left: 0,
            right: 0,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                ElevatedButton.icon(
                  style: ElevatedButton.styleFrom(backgroundColor: Colors.white),
                  onPressed: () {
                    setState(() => previewImage = null);
                  },
                  icon: const Icon(Icons.refresh, color: Colors.black),
                  label: const Text("다시 촬영", style: TextStyle(color: Colors.black)),
                ),
                ElevatedButton.icon(
                  style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFFB88C65)),
                  onPressed: () => sendToServer(previewImage!),
                  icon: const Icon(Icons.send),
                  label: const Text("전송"),
                ),
              ],
            ),
          ),
          const Positioned(
            bottom: 60,
            left: 0,
            right: 0,
            child: Center(
              child: Text(
                "선명한 사진일수록 정확도가 높아집니다",
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 14,
                  shadows: [
                    Shadow(color: Colors.black54, blurRadius: 3, offset: Offset(1, 1)),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  // ✅ 결과 화면
  Widget _buildResultView() {
  if (matchResult == null) {
    return const Center(child: Text("분석 결과가 없습니다."));
  }

  final pet = matchResult!;

  return Scaffold(
    body: Stack(
      children: [
        Positioned.fill(
          child: Container(color: Colors.black.withOpacity(0.85)),
        ),
        Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              const Icon(Icons.pets, color: Colors.greenAccent, size: 80),
              const SizedBox(height: 20),
              const Text('✅ 등록된 강아지 정보입니다!',
                  style: TextStyle(color: Colors.white, fontSize: 20)),
              const SizedBox(height: 20),
              Text("이름: ${pet['name']}", style: _infoStyle),
              Text("생일: ${pet['birth']}", style: _infoStyle),
              Text("성별: ${pet['gender']}", style: _infoStyle),
              Text("견주 전화번호: ${pet['phoneNumber']}", style: _infoStyle),
              const SizedBox(height: 40),
              ElevatedButton(
                onPressed: () {
                  setState(() {
                    previewImage = null;
                    matchResult = null;
                  });
                },
                style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFFB88C65)),
                child: const Text("카메라로 돌아가기"),
              ),
            ],
          ),
        ),
        if (isLoading)
          const Center(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                CircularProgressIndicator(color: Colors.white),
                SizedBox(height: 20),
                Text(
                  '🔍 비문 분석 중입니다...',
                  style: TextStyle(color: Colors.white, fontSize: 16),
                ),
              ],
            ),
          ),
      ],
    ),
  );
}


  TextStyle get _infoStyle => const TextStyle(color: Colors.white70, fontSize: 16);

  // 🎯 점선 원 + 텍스트
  Widget _buildCircleOverlay() {
    return Center(
      child: SizedBox(
        width: 280,
        height: 280,
        child: CustomPaint(
          painter: DottedCirclePainter(),
        ),
      ),
    );
  }

  // 📸 촬영 버튼 + 안내문구
  Widget _buildCaptureButton() {
    return Stack(
      children: [
        const Positioned(
          bottom: 100,
          left: 0,
          right: 0,
          child: Center(
            child: Text(
              "코를 원에 맞춰주세요",
              style: TextStyle(
                color: Colors.white,
                fontSize: 16,
                fontWeight: FontWeight.w500,
                shadows: [
                  Shadow(
                    color: Colors.black,
                    blurRadius: 4,
                    offset: Offset(1, 1),
                  )
                ],
              ),
            ),
          ),
        ),
        Positioned(
          bottom: 40,
          left: 0,
          right: 0,
          child: Center(
            child: FloatingActionButton(
              onPressed: _takePicture,
              backgroundColor: const Color(0xFFB88C65),
              child: const Icon(Icons.camera_alt),
            ),
          ),
        ),
      ],
    );
  }
}

// 🎨 점선 원 CustomPainter
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
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}
