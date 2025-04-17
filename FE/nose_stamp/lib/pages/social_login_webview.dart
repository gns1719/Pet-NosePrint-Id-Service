import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'package:nose_stamp/config/api_config.dart';
import 'package:nose_stamp/services/auth_service.dart';
import 'package:nose_stamp/pages/main_layout.dart';

class SocialLoginWebView extends StatefulWidget {
  final String provider;

  const SocialLoginWebView({super.key, required this.provider});

  @override
  State<SocialLoginWebView> createState() => _SocialLoginWebViewState();
}

class _SocialLoginWebViewState extends State<SocialLoginWebView> {
  late final WebViewController _controller;

  @override
  void initState() {
    super.initState();

    _controller = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setNavigationDelegate(
        NavigationDelegate(
          onPageFinished: (url) async {
            try {
              final raw = await _controller.runJavaScriptReturningResult("""
                (function() {
                  return document.body.innerText;
                })();
              """);

              final jsonStr = (raw as String)
                  .replaceAll(RegExp(r'^"|"$'), '')
                  .replaceAll(r'\"', '"');

              final Map<String, dynamic> jsonData = json.decode(jsonStr);

              if (jsonData['message'] == 'User login successful.') {
                final data = jsonData['data'];
                final accessToken = data['accessToken'];
                final refreshToken = data['refreshToken'];

                // 토큰 저장
                await AuthService().saveTokens(
                  accessToken: accessToken,
                  refreshToken: refreshToken,
                );

                if (!mounted) return;

                // MainLayout으로 이동
                Navigator.pushReplacement(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const MainLayout(),
                  ),
                );
              }
            } catch (e) {
              debugPrint("로그인 응답 파싱 실패: $e");
            }
          },
        ),
      )
      
      ..loadRequest(
        Uri.parse(ApiConfig.socialLoginUrl(widget.provider)),
      );
      
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('${widget.provider} 로그인'),
      ),
      body: WebViewWidget(controller: _controller),
    );
  }
}
