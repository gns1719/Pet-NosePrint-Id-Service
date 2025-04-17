import 'package:flutter/material.dart';
import 'pages/onboarding_page.dart';
import 'pages/main_layout.dart';

void main() {
  runApp(const KoDoJangApp());
}

class KoDoJangApp extends StatelessWidget {
  const KoDoJangApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '코도장',
      theme: ThemeData(
        fontFamily: 'Sans',
        scaffoldBackgroundColor: const Color(0xFFFFF8F0),
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFFB88C65)),
        useMaterial3: true,
      ),
      home: const OnboardingPage(),
      routes: {
        '/main': (context) => const MainLayout(),
      },
    );
  }
}