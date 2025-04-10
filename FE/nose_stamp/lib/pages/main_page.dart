import 'package:flutter/material.dart';

class MainPage extends StatelessWidget {
  final String accessToken;
  final String refreshToken;

  const MainPage({
    super.key,
    required this.accessToken,
    required this.refreshToken,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('메인 페이지')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('🎉 로그인 성공!', style: Theme.of(context).textTheme.headlineSmall),
            const SizedBox(height: 16),
            Text('Access Token: $accessToken'),
            const SizedBox(height: 8),
            Text('Refresh Token: $refreshToken'),
          ],
        ),
      ),
    );
  }
}
