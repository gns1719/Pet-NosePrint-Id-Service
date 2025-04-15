import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'main_page.dart';
import 'my_page.dart';

class MainLayout extends StatefulWidget {
  final String? accessToken;
  final String? refreshToken;

  const MainLayout({
    super.key,
    this.accessToken,
    this.refreshToken,
  });

  @override
  State<MainLayout> createState() => _MainLayoutState();
}

class _MainLayoutState extends State<MainLayout> {
  int _selectedIndex = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IndexedStack(
        index: _selectedIndex,
        children: [
          MainPage(
            accessToken: widget.accessToken,
            refreshToken: widget.refreshToken,
          ),
          const MyPage(),
        ],
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _selectedIndex,
        onDestinationSelected: (index) {
          setState(() {
            _selectedIndex = index;
          });
        },
        backgroundColor: Colors.white,
        elevation: 8,
        destinations: [
          NavigationDestination(
            icon: const Icon(Icons.pets_outlined),
            selectedIcon: const Icon(Icons.pets, color: Color(0xFFB88C65)),
            label: '메인',
          ),
          NavigationDestination(
            icon: const Icon(Icons.person_outline),
            selectedIcon: const Icon(Icons.person, color: Color(0xFFB88C65)),
            label: '마이페이지',
          ),
        ],
      ),
    );
  }
} 