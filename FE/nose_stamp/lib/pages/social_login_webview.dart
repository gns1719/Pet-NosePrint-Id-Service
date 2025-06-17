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
  bool _completed = false;

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

              String jsonStr = raw.toString()
                  .replaceAll(RegExp(r'^"|"$'), '')
                  .replaceAll(r'\\"', '"');

              // ✅ JSON만 파싱 (이제 화면에는 절대 안 뜸)
              final jsonData = json.decode(jsonStr);

              if (jsonData['message'] == 'User login successful.') {
                final accessToken = jsonData['data']['accessToken'];
                final refreshToken = jsonData['data']['refreshToken'];

                await AuthService().saveTokens(
                  accessToken: accessToken,
                  refreshToken: refreshToken,
                );

                if (!mounted) return;

                setState(() => _completed = true);

                Navigator.pushAndRemoveUntil(
                  context,
                  MaterialPageRoute(builder: (_) => const MainLayout()),
                  (route) => false,
                );
              }
            } catch (e) {
              debugPrint("❌ 로그인 파싱 실패: $e");
            }
          },
        ),
      )
      ..loadRequest(Uri.parse(ApiConfig.socialLoginUrl(widget.provider)));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('${widget.provider} 로그인')),
      body: Stack(
        children: [
          WebViewWidget(controller: _controller),
          if (_completed)
            Container(
              color: Colors.white,
            ),
        ],
      ),
    );
  }
}
