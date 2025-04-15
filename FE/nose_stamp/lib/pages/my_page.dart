import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:nose_stamp/services/auth_service.dart';
import 'package:nose_stamp/pages/login_page.dart';

class MyPage extends StatelessWidget {
  const MyPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          '마이페이지',
          style: GoogleFonts.notoSans(
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        automaticallyImplyLeading: false,
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            _buildProfileSection(),
            const Divider(height: 1),
            _buildMenuSection(context),
            const Divider(height: 1),
            _buildActivitySection(),
          ],
        ),
      ),
    );
  }

  Widget _buildProfileSection() {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Row(
        children: [
          CircleAvatar(
            radius: 40,
            backgroundColor: Colors.grey.shade200,
            child: const Icon(
              Icons.person,
              size: 40,
              color: Colors.grey,
            ),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  '사용자 이름', // TODO: 실제 사용자 이름으로 대체
                  style: GoogleFonts.notoSans(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Text(
                  'user@example.com', // TODO: 실제 이메일로 대체
                  style: GoogleFonts.notoSans(
                    fontSize: 14,
                    color: Colors.grey.shade600,
                  ),
                ),
              ],
            ),
          ),
          IconButton(
            icon: const Icon(Icons.edit),
            onPressed: () {
              // TODO: 프로필 수정 페이지로 이동
            },
          ),
        ],
      ),
    );
  }

  Widget _buildMenuSection(BuildContext context) {
    return Column(
      children: [
        ListTile(
          leading: const Icon(Icons.settings),
          title: Text(
            '설정',
            style: GoogleFonts.notoSans(),
          ),
          trailing: const Icon(Icons.chevron_right),
          onTap: () {
            // TODO: 설정 페이지로 이동
          },
        ),
        ListTile(
          leading: const Icon(Icons.logout),
          title: Text(
            '로그아웃',
            style: GoogleFonts.notoSans(),
          ),
          onTap: () {
            showDialog(
              context: context,
              builder: (context) => AlertDialog(
                title: Text(
                  '로그아웃',
                  style: GoogleFonts.notoSans(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                content: Text(
                  '정말 로그아웃 하시겠습니까?',
                  style: GoogleFonts.notoSans(),
                ),
                actions: [
                  TextButton(
                    onPressed: () {
                      Navigator.pop(context);
                    },
                    child: Text(
                      '취소',
                      style: GoogleFonts.notoSans(),
                    ),
                  ),
                  TextButton(
                        onPressed: () {
                          AuthService().clearTokens().then((_) {
                            Navigator.pushAndRemoveUntil(
                              context,
                              MaterialPageRoute(builder: (context) => const LoginPage()),
                              (route) => false,
                            );
                          });
                        },
                        child: Text(
                          '로그아웃',
                          style: GoogleFonts.notoSans(
                            color: Colors.red,
                          ),
                        ),
                      ),
                ],
              ),
            );
          },
        ),
      ],
    );
  }

  Widget _buildActivitySection() {
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '최근 활동',
            style: GoogleFonts.notoSans(
              fontSize: 18,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 16),
          _buildActivityItem(
            icon: Icons.pets,
            title: '반려동물 등록',
            description: '골든 리트리버 "멍멍이" 등록',
            date: '2024-03-20',
          ),
          _buildActivityItem(
            icon: Icons.fingerprint,
            title: '비문 등록',
            description: '"멍멍이"의 비문 등록 완료',
            date: '2024-03-20',
          ),
        ],
      ),
    );
  }

  Widget _buildActivityItem({
    required IconData icon,
    required String title,
    required String description,
    required String date,
  }) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12),
      child: Row(
        children: [
          Container(
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: const Color(0xFFFFF8F0),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Icon(icon, color: const Color(0xFFB88C65)),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: GoogleFonts.notoSans(
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Text(
                  description,
                  style: GoogleFonts.notoSans(
                    fontSize: 12,
                    color: Colors.grey.shade600,
                  ),
                ),
              ],
            ),
          ),
          Text(
            date,
            style: GoogleFonts.notoSans(
              fontSize: 12,
              color: Colors.grey.shade600,
            ),
          ),
        ],
      ),
    );
  }
} 