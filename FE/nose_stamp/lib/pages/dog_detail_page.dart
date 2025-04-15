import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'dart:io';

class DogDetailPage extends StatefulWidget {
  final Map<String, String> dogInfo;

  const DogDetailPage({
    super.key,
    required this.dogInfo,
  });

  @override
  State<DogDetailPage> createState() => _DogDetailPageState();
}

class _DogDetailPageState extends State<DogDetailPage> {
  final _formKey = GlobalKey<FormState>();
  late TextEditingController _nameController;
  late TextEditingController _birthDateController;
  late String _selectedGender;
  bool _isEditing = false;

  @override
  void initState() {
    super.initState();
    _nameController = TextEditingController(text: widget.dogInfo['name']);
    _birthDateController = TextEditingController(text: widget.dogInfo['birthDate']);
    _selectedGender = widget.dogInfo['gender'] ?? '남아';
  }

  @override
  void dispose() {
    _nameController.dispose();
    _birthDateController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: CustomScrollView(
        slivers: [
          SliverAppBar(
            expandedHeight: 300,
            pinned: true,
            flexibleSpace: FlexibleSpaceBar(
              background: widget.dogInfo['imageUrl']!.startsWith('assets/')
                  ? Image.asset(
                      widget.dogInfo['imageUrl']!,
                      fit: BoxFit.cover,
                    )
                  : Image.file(
                      File(widget.dogInfo['imageUrl']!),
                      fit: BoxFit.cover,
                    ),
            ),
            actions: [
              IconButton(
                icon: Icon(_isEditing ? Icons.save : Icons.edit),
                onPressed: () {
                  if (_isEditing) {
                    if (_formKey.currentState!.validate()) {
                      // TODO: API를 통해 정보 업데이트
                      setState(() {
                        _isEditing = false;
                      });
                    }
                  } else {
                    setState(() {
                      _isEditing = true;
                    });
                  }
                },
              ),
            ],
          ),
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '기본 정보',
                      style: GoogleFonts.notoSans(
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 24),
                    TextFormField(
                      controller: _nameController,
                      enabled: _isEditing,
                      style: GoogleFonts.notoSans(
                        fontSize: 18,
                        fontWeight: FontWeight.w500,
                      ),
                      decoration: InputDecoration(
                        labelText: '이름',
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                        filled: !_isEditing,
                        fillColor: _isEditing ? null : Colors.grey.shade100,
                      ),
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return '이름을 입력해주세요';
                        }
                        return null;
                      },
                    ),
                    const SizedBox(height: 16),
                    TextFormField(
                      controller: _birthDateController,
                      enabled: _isEditing,
                      style: GoogleFonts.notoSans(
                        fontSize: 18,
                        fontWeight: FontWeight.w500,
                      ),
                      decoration: InputDecoration(
                        labelText: '생년월일',
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                        filled: !_isEditing,
                        fillColor: _isEditing ? null : Colors.grey.shade100,
                      ),
                      validator: (value) {
                        if (value == null || value.isEmpty) {
                          return '생년월일을 입력해주세요';
                        }
                        return null;
                      },
                    ),
                    const SizedBox(height: 16),
                    DropdownButtonFormField<String>(
                      value: _selectedGender,
                      decoration: InputDecoration(
                        labelText: '성별',
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                        filled: !_isEditing,
                        fillColor: _isEditing ? null : Colors.grey.shade100,
                      ),
                      items: ['남아', '여아']
                          .map((gender) => DropdownMenuItem(
                                value: gender,
                                child: Text(
                                  gender,
                                  style: GoogleFonts.notoSans(
                                    fontSize: 18,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              ))
                          .toList(),
                      onChanged: _isEditing
                          ? (value) {
                              if (value != null) {
                                setState(() {
                                  _selectedGender = value;
                                });
                              }
                            }
                          : null,
                    ),
                    const SizedBox(height: 32),
                    Text(
                      '비문 정보',
                      style: GoogleFonts.notoSans(
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 16),
                    if (widget.dogInfo['noseprint'] == null)
                      Center(
                        child: ElevatedButton.icon(
                          onPressed: () {
                            // TODO: 비문 등록 페이지로 이동
                          },
                          icon: const Icon(Icons.fingerprint),
                          label: Text(
                            '비문 등록하기',
                            style: GoogleFonts.notoSans(
                              fontSize: 16,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFFB88C65),
                            foregroundColor: Colors.white,
                            padding: const EdgeInsets.symmetric(
                              horizontal: 24,
                              vertical: 12,
                            ),
                            shape: RoundedRectangleBorder(
                              borderRadius: BorderRadius.circular(12),
                            ),
                          ),
                        ),
                      )
                    else
                      Card(
                        child: ListTile(
                          leading: const Icon(Icons.fingerprint),
                          title: Text(
                            '등록된 비문',
                            style: GoogleFonts.notoSans(
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          subtitle: Text(
                            '최근 업데이트: ${widget.dogInfo['noseprintDate']}',
                            style: GoogleFonts.notoSans(),
                          ),
                          trailing: IconButton(
                            icon: const Icon(Icons.edit),
                            onPressed: () {
                              // TODO: 비문 수정 페이지로 이동
                            },
                          ),
                        ),
                      ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
} 