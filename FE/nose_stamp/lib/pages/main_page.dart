import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'dog_detail_page.dart';
import 'add_dog_page.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import 'package:nose_stamp/services/auth_service.dart';
import 'package:nose_stamp/config/api_config.dart';

class MainPage extends StatefulWidget {
  const MainPage({super.key});

  @override
  State<MainPage> createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  List<Map<String, dynamic>> _pets = [];
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _fetchPets();
  }

  Future<void> _fetchPets() async {
    try {
      final tokens = await AuthService().getTokens();
      final accessToken = tokens['accessToken'];

      if (accessToken == null) {
        setState(() {
          _error = '로그인이 필요합니다';
          _isLoading = false;
        });
        return;
      }
      
      final url = Uri.parse(ApiConfig.petListUrl);


      final response = await http.get(
        Uri.parse(ApiConfig.petListUrl),
        headers: {
          'Authorization': 'Bearer $accessToken',
          'Content-Type': 'application/json',
        },
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        setState(() {
          _pets = List<Map<String, dynamic>>.from(data['data']);
          _isLoading = false;
        });
      } else {
        setState(() {
          _error = '반려동물 목록을 불러오는데 실패했습니다';
          _isLoading = false;
        });
      }
    } catch (e) {
      setState(() {
        _error = '서버 연결에 실패했습니다';
        _isLoading = false;
      });
    }
  }

  Future<void> _registerPet({
    required String name,
    required String birthDate,
    required String profileUrl,
    required String gender,
  }) async {
    try {
      final tokens = await AuthService().getTokens();
      final accessToken = tokens['accessToken'];

      if (accessToken == null) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('로그인이 필요합니다')),
        );
        return;
      }

      final response = await http.post(
        Uri.parse(ApiConfig.petRegisterUrl),
        headers: {
          'Authorization': 'Bearer $accessToken',
          'Content-Type': 'application/json',
        },
        body: json.encode({
          'name': name,
          'birthDate': birthDate,
          'profileUrl': profileUrl,
          'gender': gender,
        }),
      );

      if (response.statusCode == 200) {
        _fetchPets();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('반려동물 등록에 실패했습니다')),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('서버 연결에 실패했습니다')),
      );
    }
  }

  Future<void> _updatePet({
    required int petId,
    required String name,
    required String birthDate,
    required String profileUrl,
    required String gender,
  }) async {
    try {
      final tokens = await AuthService().getTokens();
      final accessToken = tokens['accessToken'];

      if (accessToken == null) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('로그인이 필요합니다')),
        );
        return;
      }

      final response = await http.put(
        Uri.parse(ApiConfig.petUpdateUrl(petId)),
        headers: {
          'Authorization': 'Bearer $accessToken',
          'Content-Type': 'application/json',
        },
        body: json.encode({
          'name': name,
          'birthDate': birthDate,
          'profileUrl': profileUrl,
          'gender': gender,
        }),
      );

      if (response.statusCode == 200) {
        _fetchPets();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('반려동물 정보 수정에 실패했습니다')),
        );
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('서버 연결에 실패했습니다')),
      );
    }
  }

  Widget _buildAddPetCard() {
    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
      ),
      child: InkWell(
        onTap: () async {
          final result = await Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => const AddDogPage()),
          );
          if (result != null && mounted) {
            _registerPet(
              name: result['name'],
              birthDate: result['birthDate'],
              profileUrl: result['profileUrl'],
              gender: result['gender'],
            );
          }
        },
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Container(
                width: 80,
                height: 80,
                decoration: BoxDecoration(
                  color: Colors.grey.shade200,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: const Icon(
                  Icons.add,
                  size: 40,
                  color: Color(0xFFB88C65),
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '새로운 반려동물 등록',
                      style: GoogleFonts.notoSans(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '반려동물의 정보를 등록하고 관리해보세요',
                      style: GoogleFonts.notoSans(
                        fontSize: 14,
                        color: Colors.grey.shade600,
                      ),
                    ),
                  ],
                ),
              ),
              const Icon(
                Icons.chevron_right,
                color: Colors.grey,
              ),
            ],
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(
        body: Center(
          child: CircularProgressIndicator(),
        ),
      );
    }

    if (_error != null) {
      return Scaffold(
        body: Center(
          child: Text(_error!),
        ),
      );
    }

    return Scaffold(
      appBar: AppBar(
        title: Text(
          '반려동물 코지문 인식',
          style: GoogleFonts.notoSans(
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
        automaticallyImplyLeading: false,
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: _pets.length + 1,
        itemBuilder: (context, index) {
          if (index < _pets.length) {
            final pet = _pets[index];
            return Card(
              margin: const EdgeInsets.only(bottom: 16),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
              child: InkWell(
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => DogDetailPage(
                        dogInfo: {
                          'petId': pet['petId'].toString(),
                          'name': pet['name'],
                          'birthDate': pet['birth'],
                          'profileUrl': pet['profile'],
                          'gender': pet['gender'],
                        },
                        onUpdate: (updatedInfo) {
                          _updatePet(
                            petId: int.parse(updatedInfo['petId']!),
                            name: updatedInfo['name']!,
                            birthDate: updatedInfo['birthDate']!,
                            profileUrl: updatedInfo['profileUrl']!,
                            gender: updatedInfo['gender']!,
                          );
                        },
                      ),
                    ),
                  );
                },
                borderRadius: BorderRadius.circular(12),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Row(
                    children: [
                      Hero(
                        tag: 'pet_image_${pet['petId']}',
                        child: Container(
                          width: 80,
                          height: 80,
                          decoration: BoxDecoration(
                            color: Colors.grey.shade200,
                            borderRadius: BorderRadius.circular(8),
                            image: DecorationImage(
                              image: NetworkImage(pet['profile']),
                              fit: BoxFit.cover,
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(width: 16),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              pet['name'],
                              style: GoogleFonts.notoSans(
                                fontSize: 18,
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              '생일: ${pet['birth']}',
                              style: GoogleFonts.notoSans(
                                fontSize: 14,
                                color: Colors.grey.shade600,
                              ),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              '성별: ${pet['gender']}',
                              style: GoogleFonts.notoSans(
                                fontSize: 14,
                                color: Colors.grey.shade600,
                              ),
                            ),
                          ],
                        ),
                      ),
                      const Icon(
                        Icons.chevron_right,
                        color: Colors.grey,
                      ),
                    ],
                  ),
                ),
              ),
            );
          } else {
            return _buildAddPetCard();
          }
        },
      ),
    );
  }
}
