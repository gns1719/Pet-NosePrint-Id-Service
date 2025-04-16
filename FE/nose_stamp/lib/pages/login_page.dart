import 'package:flutter/material.dart';
import 'social_login_webview.dart';

class LoginPage extends StatelessWidget {
  const LoginPage({super.key});

  @override
  Widget build(BuildContext context) {
    final idController = TextEditingController();
    final pwController = TextEditingController();

    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 32),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const Center(
                child: Column(
                  children: [
                    Icon(Icons.pets, size: 64, color: Color(0xFFB88C65)),
                    SizedBox(height: 8),
                    Text(
                      '코도장',
                      style: TextStyle(
                        fontSize: 28,
                        fontWeight: FontWeight.bold,
                        color: Color(0xFF6D4C41),
                      ),
                    ),
                    SizedBox(height: 24),
                  ],
                ),
              ),
              TextField(
                controller: idController,
                decoration: InputDecoration(
                  filled: true,
                  fillColor: Colors.white,
                  labelText: '아이디',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
              ),
              const SizedBox(height: 16),
              TextField(
                controller: pwController,
                obscureText: true,
                decoration: InputDecoration(
                  filled: true,
                  fillColor: Colors.white,
                  labelText: '비밀번호',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                ),
              ),
              const SizedBox(height: 8),
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  TextButton(
                    onPressed: () {},
                    style: TextButton.styleFrom(foregroundColor: Colors.brown),
                    child: const Text('회원가입'),
                  ),
                  const Text('|'),
                  TextButton(
                    onPressed: () {},
                    style: TextButton.styleFrom(foregroundColor: Colors.brown),
                    child: const Text('비밀번호 찾기'),
                  ),
                ],
              ),
              // 로그인 버튼 추가 부분
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: () {
                  // 일반 로그인 처리 로직 작성 예정
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFFB88C65),
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 14),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                  elevation: 3,
                ),
                child: const Text('로그인', style: TextStyle(fontSize: 16)),
              ),
              const SizedBox(height: 16),

              const Divider(height: 32, color: Color(0xFFE0D4C3)),
              const SizedBox(height: 12),

              _SocialLoginButton(
                label: '카카오로 로그인',
                color: const Color(0xFFFEE500),
                textColor: Colors.black87,
                imagePath: 'assets/images/kakao_logo.png',
                provider: 'kakao',
              ),
              const SizedBox(height: 12),
              _SocialLoginButton(
                label: '네이버로 로그인',
                color: const Color(0xFF03C75A),
                textColor: Colors.white,
                imagePath: 'assets/images/naver_logo.png',
                provider: 'naver',
              ),
              const SizedBox(height: 12),
              _SocialLoginButton(
                label: 'Google 계정으로 가입',
                color: Colors.white,
                textColor: Colors.black87,
                imagePath: 'assets/images/google_logo.png',
                provider: 'google',
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _SocialLoginButton extends StatelessWidget {
  final String label;
  final Color color;
  final Color textColor;
  final String imagePath;
  final String provider;

  const _SocialLoginButton({
    required this.label,
    required this.color,
    required this.textColor,
    required this.imagePath,
    required this.provider,
  });

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: () {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (context) => SocialLoginWebView(provider: provider),
          ),
        );
      },
      style: ElevatedButton.styleFrom(
        backgroundColor: color,
        foregroundColor: textColor,
        padding: const EdgeInsets.symmetric(vertical: 14),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        shadowColor: Colors.black26,
        elevation: 3,
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Image.asset(imagePath, height: 28),
          const SizedBox(width: 12),
          Text(label, style: const TextStyle(fontSize: 16)),
        ],
      ),
    );
  }
}