class ApiConfig {
  static const String baseUrl = 'https://www.azzul.site';

  // Auth endpoints
  static String get loginUrl => '$baseUrl/users/local/login';
  static String get signupUrl => '$baseUrl/users/local/signup';
  static String get checkIdUrl => '$baseUrl/users/local/check-id';
  static String get passwordResetUrl => '$baseUrl/users/local/find-pw';

  // Pet endpoints
  static String get petListUrl => '$baseUrl/pets/list';
  static String get petRegisterUrl => '$baseUrl/pets/register';
  static String petUpdateUrl(int petId) => '$baseUrl/pets/$petId/update';
  static String petProfileUrl(int petId) => '$baseUrl/pets/$petId/profile-url';

  //user endpoints
  static String get getUserUrl => '$baseUrl/users/getInfo';
  static String get updateUserUrl => '$baseUrl/users/update-profile';
  static String get changePasswordUrl => '$baseUrl/users/change-password';

  //Pet nosePrint
  static String nosePresignedUrl(String fileName) => '$baseUrl/images/nosePresignedUrl?fileName=$fileName';
  static String noseUploadUrl(String fileName) => '$baseUrl/pets/register/nose?fileName=$fileName';
  static String get noseCheckPresignedUrl => '$baseUrl/images/noseCheckPresignedUrl';
  static String get noseAnalysisUrl => '$baseUrl/pets/noseAnalysisUrl';
  
  

  // Image endpoints
  static String presignedUrl(String fileName) => '$baseUrl/images/presigned-url?fileName=$fileName';

  // Social login endpoints
  static String socialLoginUrl(String provider) => '$baseUrl/users/oauth/$provider/url';
} 