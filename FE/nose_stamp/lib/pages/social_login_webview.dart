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
            // 여기서 서버 콜백 URL 감지 및 디버그 로그 출력
            debugPrint("🔁 이동 URL: ${request.url}");

            final uri = Uri.parse(request.url);
            if (uri.path.contains("/users/oauth/kakao/callback")) {
              debugPrint("🎯 콜백 URL 감지됨. 서버에 요청 보내는 중...");
              // 여기에 필요한 추가 처리가 있으면 넣을 수 있어
              // ex. query 파라미터 수동 추출 등
            }

            return NavigationDecision.navigate;
          },
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

                // 메인 화면으로 이동
                Navigator.pushReplacement(
                  context,
                  MaterialPageRoute(
                    builder: (context) => const MainLayout(),
                  ),
                );
              }
            } catch (e) {
              debugPrint("❌ 로그인 응답 파싱 실패: $e");
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
