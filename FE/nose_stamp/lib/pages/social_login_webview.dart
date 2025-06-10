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
          onNavigationRequest: (request) {
            debugPrint("🔁 이동 URL: ${request.url}");
            return NavigationDecision.navigate;
          },
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

              // JSON이 아닐 경우 무시 (HTML은 WebView에서 보여지기만 하면 됨)
              Map<String, dynamic> jsonData;
              try {
                jsonData = json.decode(jsonStr);
              } catch (_) {
                return;
              }

              if (jsonData['message'] == 'User login successful.') {
                final data = jsonData['data'];
                final accessToken = data['accessToken'];
                final refreshToken = data['refreshToken'];

                await AuthService().saveTokens(
                  accessToken: accessToken,
                  refreshToken: refreshToken,
                );

                if (!mounted) return;

                Navigator.pushAndRemoveUntil(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const MainLayout(),
                  ),
                  (route) => false,
                );
              }
            } catch (e) {
              debugPrint("❌ 로그인 응답 파싱 중 예외: $e");
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
