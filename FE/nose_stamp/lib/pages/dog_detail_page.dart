import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:intl/intl.dart';

class DogDetailPage extends StatefulWidget {
  final Map<String, String> dogInfo;
  final Function(Map<String, String>) onUpdate;

  const DogDetailPage({
    super.key,
    required this.dogInfo,
    required this.onUpdate,
  });

  @override
  State<DogDetailPage> createState() => _DogDetailPageState();
}

class _DogDetailPageState extends State<DogDetailPage> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _nameController;
  late final TextEditingController _profileUrlController;
  DateTime? _birthDate;
  late String _gender;
  bool _isEditing = false;

  @override
  void initState() {
    super.initState();
    _nameController = TextEditingController(text: widget.dogInfo['name']);
    _profileUrlController = TextEditingController(text: widget.dogInfo['profileUrl']);
    _birthDate = DateFormat('yyyy-MM-dd').parse(widget.dogInfo['birthDate']!);
    _gender = widget.dogInfo['gender']!;
  }

  @override
  void dispose() {
    _nameController.dispose();
    _profileUrlController.dispose();
    super.dispose();
  }

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: _birthDate ?? DateTime.now(),
      firstDate: DateTime(2000),
      lastDate: DateTime.now(),
    );
    if (picked != null && picked != _birthDate) {
      setState(() {
        _birthDate = picked;
      });
    }
  }

  void _toggleEdit() {
    setState(() {
      if (_isEditing) {
        // 수정 취소시 원래 값으로 복원
        _nameController.text = widget.dogInfo['name']!;
        _profileUrlController.text = widget.dogInfo['profileUrl']!;
        _birthDate = DateFormat('yyyy-MM-dd').parse(widget.dogInfo['birthDate']!);
        _gender = widget.dogInfo['gender']!;
      }
      _isEditing = !_isEditing;
    });
  }

  void _saveChanges() {
    if (_formKey.currentState!.validate() && _birthDate != null) {
      widget.onUpdate({
        'petId': widget.dogInfo['petId']!,
        'name': _nameController.text,
        'birthDate': DateFormat('yyyy-MM-dd').format(_birthDate!),
        'profileUrl': _profileUrlController.text,
        'gender': _gender,
      });
      _toggleEdit();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          '반려동물 정보',
          style: GoogleFonts.notoSans(
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(_isEditing ? Icons.close : Icons.edit),
            onPressed: _toggleEdit,
          ),
          if (_isEditing)
            IconButton(
              icon: const Icon(Icons.check),
              onPressed: _saveChanges,
            ),
        ],
      ),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Hero(
                  tag: 'pet_image_${widget.dogInfo['petId']}',
                  child: GestureDetector(
                    onTap: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => Scaffold(
                            backgroundColor: Colors.black,
                            appBar: AppBar(
                              backgroundColor: Colors.black,
                              iconTheme: const IconThemeData(color: Colors.white),
                            ),
                            body: Center(
                              child: InteractiveViewer(
                                minScale: 0.5,
                                maxScale: 4.0,
                                child: Image.network(
                                  _profileUrlController.text,
                                  fit: BoxFit.contain,
                                ),
                              ),
                            ),
                          ),
                        ),
                      );
                    },
                    child: Container(
                      width: double.infinity,
                      height: 300,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(12),
                        image: DecorationImage(
                          image: NetworkImage(_profileUrlController.text),
                          fit: BoxFit.cover,
                        ),
                      ),
                    ),
                  ),
                ),
                const SizedBox(height: 24),
                if (_isEditing) ...[
                  TextFormField(
                    controller: _nameController,
                    decoration: InputDecoration(
                      labelText: '이름',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
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
                    controller: _profileUrlController,
                    decoration: InputDecoration(
                      labelText: '프로필 이미지 URL',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                    ),
                   validator: (value) {
                      if (value == null || value.isEmpty) {
                        return '프로필 이미지 URL을 입력해주세요';
                      }
                      final uri = Uri.tryParse(value);
                      if (uri == null || !uri.hasAbsolutePath) {
                        return '올바른 URL을 입력해주세요';
                      }
                      return null;
                    },
                  ),
                  const SizedBox(height: 16),
                  InkWell(
                    onTap: () => _selectDate(context),
                    child: InputDecorator(
                      decoration: InputDecoration(
                        labelText: '생일',
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                      ),
                      child: Text(
                        DateFormat('yyyy-MM-dd').format(_birthDate!),
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                  InputDecorator(
                    decoration: InputDecoration(
                      labelText: '성별',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                    ),
                    child: DropdownButtonHideUnderline(
                      child: DropdownButton<String>(
                        value: _gender,
                        isExpanded: true,
                        items: ['남아', '여아'].map((String value) {
                          return DropdownMenuItem<String>(
                            value: value,
                            child: Text(value),
                          );
                        }).toList(),
                        onChanged: (String? newValue) {
                          if (newValue != null) {
                            setState(() {
                              _gender = newValue;
                            });
                          }
                        },
                      ),
                    ),
                  ),
                ] else ...[
                  ListTile(
                    title: const Text('이름'),
                    subtitle: Text(
                      _nameController.text,
                      style: GoogleFonts.notoSans(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                  ListTile(
                    title: const Text('생일'),
                    subtitle: Text(
                      DateFormat('yyyy-MM-dd').format(_birthDate!),
                      style: GoogleFonts.notoSans(
                        fontSize: 16,
                      ),
                    ),
                  ),
                  ListTile(
                    title: const Text('성별'),
                    subtitle: Text(
                      _gender,
                      style: GoogleFonts.notoSans(
                        fontSize: 16,
                      ),
                    ),
                  ),
                ],
              ],
            ),
          ),
        ),
      ),
    );
  }
} 