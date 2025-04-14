import 'package:flutter/material.dart';
import 'pages/login_page.dart';

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
      home: const LoginPage(),
    );
  }
}
