
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:intl/intl.dart';
import 'package:image_picker/image_picker.dart';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:nose_stamp/config/api_config.dart';
import 'package:nose_stamp/services/auth_service.dart';
import 'nose_stamp_register_page.dart';

class DogDetailPage extends StatefulWidget {
  final Map<String, String> dogInfo;

  const DogDetailPage({
    super.key,
    required this.dogInfo,
  });

  @override
  State<DogDetailPage> createState() => _DogDetailPageState();
}

class _DogDetailPageState extends State<DogDetailPage> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _nameController;
  late final TextEditingController _profileUrlController;
  DateTime? _birthDate;
  late String _gender;
  bool _isEditing = false;
  bool _isLoading = false;
  File? _selectedImage;
  final ImagePicker _picker = ImagePicker();

  @override
  void initState() {
    super.initState();
    _nameController = TextEditingController(text: widget.dogInfo['name']);
    _profileUrlController = TextEditingController(text: widget.dogInfo['profileUrl']);
    _birthDate = DateFormat('yyyy-MM-dd').parse(widget.dogInfo['birthDate']!);
    _gender = widget.dogInfo['gender']!;
  }

  @override
  void dispose() {
    _nameController.dispose();
    _profileUrlController.dispose();
    super.dispose();
  }

  Future<void> _selectImage() async {
    final XFile? image = await showDialog<XFile>(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: Text(
            '이미지 선택',
            style: GoogleFonts.notoSans(fontWeight: FontWeight.bold),
          ),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              ListTile(
                leading: const Icon(Icons.camera_alt),
                title: const Text('카메라로 촬영'),
                onTap: () async {
                  Navigator.pop(
                    context,
                    await _picker.pickImage(source: ImageSource.camera),
                  );
                },
              ),
              ListTile(
                leading: const Icon(Icons.photo_library),
                title: const Text('갤러리에서 선택'),
                onTap: () async {
                  Navigator.pop(
                    context,
                    await _picker.pickImage(source: ImageSource.gallery),
                  );
                },
              ),
            ],
          ),
        );
      },
    );

    if (image != null) {
      setState(() {
        _selectedImage = File(image.path);
      });
    }
  }

  Future<void> _uploadImage() async {
    if (_selectedImage == null) return;

    try {
      final tokens = await AuthService().getTokens();
      final accessToken = tokens['accessToken'];

      if (accessToken == null) {
        throw Exception('로그인이 필요합니다');
      }

      final presignedUrlResponse = await http.get(
        Uri.parse(ApiConfig.presignedUrl(widget.dogInfo['petId']!)),
        headers: {
          'Authorization': 'Bearer $accessToken',
          'Content-Type': 'application/json',
        },
      );

      if (presignedUrlResponse.statusCode != 200) {
        throw Exception('이미지 업로드 URL 생성에 실패했습니다');
      }

      final presignedUrl = presignedUrlResponse.body;

      final imageBytes = await _selectedImage!.readAsBytes();
      final uploadResponse = await http.put(
        Uri.parse(presignedUrl),
        headers: {
          'Content-Type': 'image/jpeg',
        },
        body: imageBytes,
      );

      if (uploadResponse.statusCode != 200) {
        throw Exception('이미지 업로드에 실패했습니다');
      }

      final String baseImageUrl = presignedUrl.split('?').first;
      final int timestamp = DateTime.now().millisecondsSinceEpoch;
      final String imageUrlWithTimestamp = '$baseImageUrl?ts=$timestamp';

      final petIdRaw = widget.dogInfo['petId'];
      final int petId = int.tryParse(petIdRaw.toString()) ?? (throw Exception("petId 형변환 실패"));

      final updateResponse = await http.patch(
        Uri.parse(ApiConfig.petProfileUrl(petId)),
        headers: {
          'Authorization': 'Bearer ' + accessToken,
          'Content-Type': 'application/json',
        },
        body: json.encode({
          'profile': imageUrlWithTimestamp,
        }),
      );

      if (updateResponse.statusCode != 200) {
        throw Exception('프로필 이미지 URL 업데이트 실패');
      }
      setState(() {
        _profileUrlController.text = imageUrlWithTimestamp;
      });
    } catch (e) {
      if (mounted) {
        print(e.toString());
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(e.toString())),
        );
      }
    }
  }

  Future<void> _saveChanges() async {
    if (!_formKey.currentState!.validate() || _birthDate == null) {
      return;
    }

    setState(() {
      _isLoading = true;
    });

    try {
      final tokens = await AuthService().getTokens();
      final accessToken = tokens['accessToken'];

      if (accessToken == null) {
        throw Exception('로그인이 필요합니다');
      }

      if (_selectedImage != null) {
        await _uploadImage();
      }

      final response = await http.put(
        Uri.parse(ApiConfig.petUpdateUrl(int.parse(widget.dogInfo['petId']!))),
        headers: {
          'Authorization': 'Bearer ' + accessToken,
          'Content-Type': 'application/json',
        },
        body: json.encode({
          'name': _nameController.text,
          'birth': DateFormat('yyyy-MM-dd').format(_birthDate!),
          'gender': _gender,
        }),
      );

      if (response.statusCode != 200) {
        throw Exception('반려동물 정보 수정에 실패했습니다');
      }

      if (mounted) {
        _toggleEdit();
        Navigator.pop(context);
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(e.toString())),
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  void _toggleEdit() {
    setState(() {
      if (_isEditing) {
        _nameController.text = widget.dogInfo['name']!;
        _profileUrlController.text = widget.dogInfo['profileUrl']!;
        _birthDate = DateFormat('yyyy-MM-dd').parse(widget.dogInfo['birthDate']!);
        _gender = widget.dogInfo['gender']!;
        _selectedImage = null;
      }
      _isEditing = !_isEditing;
    });
  }

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: _birthDate ?? DateTime.now(),
      firstDate: DateTime(2000),
      lastDate: DateTime.now(),
    );
    if (picked != null && picked != _birthDate) {
      setState(() {
        _birthDate = picked;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          '반려동물 정보',
          style: GoogleFonts.notoSans(
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(_isEditing ? Icons.close : Icons.edit),
            onPressed: _isLoading ? null : _toggleEdit,
          ),
          if (_isEditing)
            IconButton(
              icon: const Icon(Icons.check),
              onPressed: _isLoading ? null : _saveChanges,
            ),
        ],
      ),
      body: Stack(
        children: [
          SingleChildScrollView(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Hero(
                      tag: 'pet_image_${widget.dogInfo['petId']}',
                      child: GestureDetector(
                        onTap: _isEditing ? _selectImage : () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => Scaffold(
                                backgroundColor: Colors.black,
                                appBar: AppBar(
                                  backgroundColor: Colors.black,
                                  iconTheme: const IconThemeData(color: Colors.white),
                                ),
                                body: Center(
                                  child: InteractiveViewer(
                                    minScale: 0.5,
                                    maxScale: 4.0,
                                    child: Image.network(_profileUrlController.text,
                                      fit: BoxFit.contain,
                                    ),
                                  ),
                                ),
                              ),
                            ),
                          );
                        },
                        child: Stack(
                          children: [
                            Container(
                              width: double.infinity,
                              height: 300,
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(12),
                                image: DecorationImage(
                                  image: _selectedImage != null
                                      ? FileImage(_selectedImage!) as ImageProvider
                                      : NetworkImage(_profileUrlController.text),
                                  fit: BoxFit.cover,
                                ),
                              ),
                            ),
                            if (_isEditing)
                              Positioned(
                                right: 16,
                                bottom: 16,
                                child: CircleAvatar(
                                  backgroundColor: const Color(0xFFB88C65),
                                  child: IconButton(
                                    icon: const Icon(Icons.camera_alt, color: Colors.white),
                                    onPressed: _selectImage,
                                  ),
                                ),
                              ),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(height: 24),
                    if (_isEditing) ...[
                      TextFormField(
                        controller: _nameController,
                        decoration: InputDecoration(
                          labelText: '이름',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(12),
                          ),
                        ),
                        validator: (value) {
                          if (value == null || value.isEmpty) {
                            return '이름을 입력해주세요';
                          }
                          return null;
                        },
                      ),
                      const SizedBox(height: 16),
                      InkWell(
                        onTap: () => _selectDate(context),
                        child: InputDecorator(
                          decoration: InputDecoration(
                            labelText: '생일',
                            border: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(12),
                            ),
                          ),
                          child: Text(
                            DateFormat('yyyy-MM-dd').format(_birthDate!),
                          ),
                        ),
                      ),
                      const SizedBox(height: 16),
                      InputDecorator(
                        decoration: InputDecoration(
                          labelText: '성별',
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(12),
                          ),
                        ),
                        child: DropdownButtonHideUnderline(
                          child: DropdownButton<String>(
                            value: _gender,
                            isExpanded: true,
                            items: ['남아', '여아'].map((String value) {
                              return DropdownMenuItem<String>(
                                value: value,
                                child: Text(value),
                              );
                            }).toList(),
                            onChanged: (String? newValue) {
                              if (newValue != null) {
                                setState(() {
                                  _gender = newValue;
                                });
                              }
                            },
                          ),
                        ),
                      ),
                    ] else ...[
                      ListTile(
                        title: const Text('이름'),
                        subtitle: Text(
                          _nameController.text,
                          style: GoogleFonts.notoSans(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      ListTile(
                        title: const Text('생일'),
                        subtitle: Text(
                          DateFormat('yyyy-MM-dd').format(_birthDate!),
                          style: GoogleFonts.notoSans(
                            fontSize: 16,
                          ),
                        ),
                      ),
                      ListTile(
                        title: const Text('성별'),
                        subtitle: Text(
                          _gender,
                          style: GoogleFonts.notoSans(
                            fontSize: 16,
                          ),
                        ),
                      ),
                      const SizedBox(height: 24),
                      ElevatedButton.icon(
                        onPressed: () async {
                          final result = await Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (_) => NoseStampRegisterPage(
                                petId: widget.dogInfo['petId'].toString(),
                              ),
                            ),

                          );
                          if (result == true) {
                            debugPrint("비문 등록 완료됨 🐶");
                          }
                        },
                        icon: const Icon(Icons.camera_alt),
                        label: const Text('강아지 비문 등록'),
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFFB88C65),
                          foregroundColor: Colors.white,
                          padding: const EdgeInsets.symmetric(vertical: 14),
                          textStyle: GoogleFonts.notoSans(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                          ),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(12),
                          ),
                        ),
                      ),
                    ],
                  ],
                ),
              ),
            ),
          ),
          if (_isLoading)
            Container(
              color: Colors.black54,
              child: Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const CircularProgressIndicator(
                      valueColor: AlwaysStoppedAnimation<Color>(Color(0xFFB88C65)),
                    ),
                    const SizedBox(height: 16),
                    Text(
                      '반려동물 정보를 수정 중입니다...',
                      style: GoogleFonts.notoSans(
                        color: Colors.white,
                        fontSize: 16,
                      ),
                    ),
                  ],
                ),
              ),
            ),
        ],
      ),
    );
  }
}
