import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'main_page.dart';

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

                if (!mounted) return;

                Navigator.pushReplacement(
                  context,
                  MaterialPageRoute(
                    builder: (context) => MainPage(
                      accessToken: accessToken,
                      refreshToken: refreshToken,
                    ),
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
        Uri.parse('http://10.101.25.75:8080/users/oauth/${widget.provider}'),
      );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('${widget.provider} 로그인 중'),
      ),
      body: WebViewWidget(controller: _controller),
    );
  }
}
