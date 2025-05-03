import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:nose_stamp/services/auth_service.dart';
import 'package:nose_stamp/pages/login_page.dart';
import 'package:nose_stamp/config/api_config.dart';
import 'dart:convert';
import 'package:http/http.dart' as http;

class MyPage extends StatefulWidget {
  const MyPage({super.key});

  @override
  State<MyPage> createState() => _MyPageState();
}

class _MyPageState extends State<MyPage> {
  final emailController = TextEditingController();
  final phoneController = TextEditingController();
  final nameController = TextEditingController();
  String userType = ''; // LOCAL or OAUTH

  @override
  void initState() {
    super.initState();
    fetchUserProfile();
  }

  Future<String> getAccessTokenOrThrow() async {
    final tokens = await AuthService().getTokens();
    final accessToken = tokens['accessToken'];
    if (accessToken == null) throw Exception('로그인이 필요합니다');
    return accessToken;
  }

  Future<void> fetchUserProfile() async {
    final accessToken = await getAccessTokenOrThrow();
    final response = await http.get(
      Uri.parse(ApiConfig.getUserUrl),
      headers: {
        'Authorization': 'Bearer $accessToken',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      setState(() {
        emailController.text = data['email'] ?? '';
        phoneController.text = data['phoneNumber'] ?? '';
        nameController.text = data['name'] ?? '';
        userType = data['loginType'] ?? '';
      });
    } else {
      throw Exception('사용자 정보 불러오기 실패');
    }
  }

  Future<void> updateUserProfile() async {
    final accessToken = await getAccessTokenOrThrow();
    final body = jsonEncode({
      'email': emailController.text.trim(),
      'phone': phoneController.text.trim(),
    });

    final response = await http.patch(
      Uri.parse(ApiConfig.updateUserUrl),
      headers: {
        'Authorization': 'Bearer $accessToken',
        'Content-Type': 'application/json',
      },
      body: body,
    );

    if (response.statusCode != 200) {
      throw Exception('정보 수정 실패: ${response.statusCode} ${response.body}');
    }
  }

  Future<void> showEditDialog(String fieldName, TextEditingController controller) async {
    final tempController = TextEditingController(text: controller.text);

    final result = await showDialog<String>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('$fieldName 변경'),
        content: TextField(
          controller: tempController,
          decoration: InputDecoration(labelText: fieldName),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('취소'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, tempController.text),
            child: const Text('저장'),
          ),
        ],
      ),
    );

    if (result != null && result != controller.text) {
      setState(() => controller.text = result);
      await updateUserProfile();
    }
  }

  void _showPasswordChangeDialog() {
    final currentPwController = TextEditingController();
    final newPwController = TextEditingController();
    final confirmPwController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('비밀번호 변경'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: currentPwController,
              obscureText: true,
              decoration: const InputDecoration(labelText: '현재 비밀번호'),
            ),
            TextField(
              controller: newPwController,
              obscureText: true,
              decoration: const InputDecoration(labelText: '새 비밀번호'),
            ),
            TextField(
              controller: confirmPwController,
              obscureText: true,
              decoration: const InputDecoration(labelText: '새 비밀번호 확인'),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('취소'),
          ),
          TextButton(
            onPressed: () async {
              final current = currentPwController.text.trim();
              final newPw = newPwController.text.trim();
              final confirm = confirmPwController.text.trim();

              if (newPw != confirm) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('새 비밀번호가 일치하지 않습니다.')),
                );
                return;
              }

              try {
                await _changePassword(current, newPw);
                Navigator.pop(context);
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('비밀번호가 변경되었습니다.')),
                );
              } catch (e) {
                Navigator.pop(context);
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('변경 실패: $e')),
                );
              }
            },
            child: const Text('변경'),
          ),
        ],
      ),
    );
  }

  Future<void> _changePassword(String currentPw, String newPw) async {
    final accessToken = await getAccessTokenOrThrow();
    final body = jsonEncode({
      'currentPassword': currentPw,
      'newPassword': newPw,
    });

    final response = await http.patch(
      Uri.parse(ApiConfig.changePasswordUrl), // API 주소 맞게 수정
      headers: {
        'Authorization': 'Bearer $accessToken',
        'Content-Type': 'application/json',
      },
      body: body,
    );

    if (response.statusCode != 200) {
      throw Exception('상태 코드: ${response.statusCode} - ${response.body}');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('')),
      body: Column(
        children: [
          Expanded(
            child: SingleChildScrollView(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
              child: Column(
                children: [
                  _buildProfileGreeting(),
                  const SizedBox(height: 20),
                  _buildInfoField('이름', nameController.text, readOnly: true),
                  _buildInfoField(
                    '연락처',
                    phoneController.text,
                    placeholder: '핸드폰 번호를 추가해주세요',
                    actionLabel: '변경',
                    onEdit: () => showEditDialog('연락처', phoneController),
                  ),
                  _buildInfoField(
                    '이메일',
                    emailController.text,
                    placeholder: '이메일을 추가해 보세요',
                    actionLabel: '변경',
                    onEdit: () => showEditDialog('이메일', emailController),
                  ),
                  const SizedBox(height: 30),
                ],
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.only(bottom: 20),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                if (userType == 'LOCAL') ...[
                  GestureDetector(
                    onTap: _showPasswordChangeDialog,
                    child: Text(
                      '비밀번호 변경',
                      style: GoogleFonts.notoSans(
                        color: Colors.blue,
                        decoration: TextDecoration.underline,
                      ),
                    ),
                  ),
                  const Text(' / ', style: TextStyle(color: Colors.grey)),
                ],
                GestureDetector(
                  onTap: () async {
                    final confirmed = await showDialog<bool>(
                      context: context,
                      builder: (context) => AlertDialog(
                        title: const Text('로그아웃'),
                        content: const Text('정말 로그아웃 하시겠습니까?'),
                        actions: [
                          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('취소')),
                          TextButton(
                            onPressed: () => Navigator.pop(context, true),
                            child: const Text('로그아웃', style: TextStyle(color: Colors.red)),
                          ),
                        ],
                      ),
                    );

                    if (confirmed == true) {
                      await AuthService().clearTokens();
                      Navigator.pushAndRemoveUntil(
                        context,
                        MaterialPageRoute(builder: (_) => const LoginPage()),
                        (route) => false,
                      );
                    }
                  },
                  child: Text(
                    '로그아웃',
                    style: GoogleFonts.notoSans(
                      color: Colors.red,
                      decoration: TextDecoration.underline,
                    ),
                  ),
                ),
              ],
            ),
          ),

        ],
      ),
    );
  }

  Widget _buildProfileGreeting() {
    return Column(
      children: [
        Stack(
          alignment: Alignment.bottomRight,
          children: [
            const CircleAvatar(
              radius: 50,
              backgroundColor: Colors.grey,
              child: Icon(Icons.person, size: 50, color: Colors.white),
            ),
            Positioned(
              bottom: 0,
              right: 4,
              child: CircleAvatar(
                radius: 16,
                backgroundColor: Colors.white,
                child: Icon(Icons.camera_alt, size: 16, color: Colors.grey),
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        Text(
          '반가워요 보호자님,\n오늘도 반려동물과 함께 즐거운 하루 보내세요!',
          style: GoogleFonts.notoSans(fontSize: 14),
          textAlign: TextAlign.center,
        ),
        const SizedBox(height: 16),
      ],
    );
  }

  Widget _buildInfoField(String label, String value,
      {bool readOnly = false, String? placeholder, String? actionLabel, VoidCallback? onEdit}) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(label, style: GoogleFonts.notoSans(fontSize: 13, color: Colors.black87)),
                const SizedBox(height: 4),
                Text(
                  value.isEmpty ? (placeholder ?? '') : value,
                  style: GoogleFonts.notoSans(
                    fontSize: 16,
                    fontWeight: value.isEmpty ? FontWeight.normal : FontWeight.bold,
                    color: value.isEmpty ? Colors.grey : Colors.black,
                  ),
                ),
              ],
            ),
          ),
          if (actionLabel != null)
            TextButton(
              onPressed: onEdit,
              child: Text(actionLabel, style: const TextStyle(color: Colors.deepOrange)),
            ),
        ],
      ),
    );
  }
}
