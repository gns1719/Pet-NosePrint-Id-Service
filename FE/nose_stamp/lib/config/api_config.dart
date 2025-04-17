class ApiConfig {
  static const String baseUrl = 'http://172.30.1.63:8080';

  // Auth endpoints
  static String get loginUrl => '$baseUrl/users/local/login';
  static String get signupUrl => '$baseUrl/users/local/signup';
  static String get checkIdUrl => '$baseUrl/users/local/check-id';

  // Pet endpoints
  static String get petListUrl => '$baseUrl/pets/list';
  static String get petRegisterUrl => '$baseUrl/pets/register';
  static String petUpdateUrl(int petId) => '$baseUrl/pets/$petId';
} 