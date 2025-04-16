class ApiConfig {
  static const String baseUrl = 'http://192.168.0.6:8080';

  // Auth endpoints
  static String get loginUrl => '$baseUrl/users/local/login';
  static String get signupUrl => '$baseUrl/users/local/signup';
  static String get checkIdUrl => '$baseUrl/users/local/check-id';

  // Pet endpoints
  static String get petListUrl => '$baseUrl/pets/list';
  static String get petRegisterUrl => '$baseUrl/pets/register';
  static String petUpdateUrl(int petId) => '$baseUrl/pets/$petId';
  static String petProfileUrl(int petId) => '$baseUrl/pets/$petId/profile-url';

  // Image endpoints
  static String presignedUrl(String fileName) => '$baseUrl/images/presigned-url?fileName=$fileName';
} 