import 'package:flutter/material.dart';
import 'main_page.dart';
import 'my_page.dart';
import 'nose_scan_page.dart';

class MainLayout extends StatefulWidget {
  const MainLayout({super.key});

  @override
  State<MainLayout> createState() => _MainLayoutState();
}

class _MainLayoutState extends State<MainLayout> {
  int _selectedIndex = 0;

  final List<Widget> _pages = const [
    MainPage(),
    SizedBox.shrink(),
    MyPage(),
  ];

  void _onDestinationSelected(int index) {
    if (index == 1) {
      debugPrint('🐶 코 스캔 탭 선택됨');
      Navigator.push(
        context,
        MaterialPageRoute(builder: (_) => const NoseScanPage()),
      );
      return;
    }

    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: IndexedStack(
        index: _selectedIndex,
        children: _pages,
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _selectedIndex,
        onDestinationSelected: _onDestinationSelected,
        backgroundColor: Colors.white,
        elevation: 8,
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.pets_outlined),
            selectedIcon: Icon(Icons.pets, color: Color(0xFFB88C65)),
            label: '메인',
          ),
          NavigationDestination(
            icon: Icon(Icons.camera_alt_outlined),
            selectedIcon: Icon(Icons.camera_alt, color: Color(0xFFB88C65)),
            label: '코 스캔',
          ),
          NavigationDestination(
            icon: Icon(Icons.person_outline),
            selectedIcon: Icon(Icons.person, color: Color(0xFFB88C65)),
            label: '마이페이지',
          ),
        ],
      ),
    );
  }
}
