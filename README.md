<<<<<<< develop
# 우송대학교 2025년도 1학기 캡스톤 디자인 1

<div align="center" >
<h2>비문을 이용한 반려동물 확인 서비스</h2>
<h4> 2025년  03월   14일 ~   2025년   06월  20일 </h4>
</div>
<br/>
 

## 📝 개요 <a name = "outline"></a>

#### 이 앱은 반려동물의 비문(코 주름) 패턴을 이용한 생체인증 기술을 활용하여 신뢰할 수 있는 반려동물 인증 시스템을 제공합니다.


## 🚀 주요 기능

- 🔍 **비문 인식 및 등록**: 반려동물의 비문을 스캔하여 고유한 생체정보 등록
- ✅ **빠르고 정확한 인증**: 비문 비교를 통해 반려동물의 신원 확인
- 🛡 **유기동물 방지**: 등록된 정보로 주인을 추적하여 반려동물 분실 및 유기 방지

이 앱을 통해 반려동물 관리의 신뢰성과 효율성을 높이고, 유기동물 문제 해결에 기여하고자 합니다.

<br/>
<div ><h1>📚 STACKS</h1></div>

<div> 
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
  <img src="https://img.shields.io/badge/Dart-0175C2?style=for-the-badge&logo=Dart&logoColor=white">
  <img src="https://img.shields.io/badge/python-3776AB?style=for-the-badge&logo=python&logoColor=white"> 
  <br>
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> 
  <img src="https://img.shields.io/badge/Redis-FF4438?style=for-the-badge&logo=Redis&logoColor=white">
  <img src="https://img.shields.io/badge/aws-232F3E?style=for-the-badge&logo=AWS&logoColor=white"> 
  <br>
  <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> 
  <img src="https://img.shields.io/badge/flutter-02569B?style=for-the-badge&logo=flutter&logoColor=white">
  <img src="https://img.shields.io/badge/yolo-111F68?style=for-the-badge&logo=yolo&logoColor=white">
  <br>
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
  <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
  <br>
</div>



<br/>

### 디렉토리 구조

```
├───AI                            
├───BE                              
│   └───src
│       ├───docs               # Spring REST Docs 등의 API 문서 디렉토리
│       ├───main
│       │   ├───config        # 전역 설정 (보안, WebMvc 등)
│       │   ├───controller    # API 요청을 처리하는 컨트롤러 계층
│       │   ├───domain        # JPA Entity 등 핵심 도메인 모델
│       │   ├───dto           # 요청/응답 데이터 전달 객체
│       │   ├───redis         # Redis 관련 설정 및 구현
│       │   ├───repository    # 데이터 접근 계층 (JPA 인터페이스)
│       │   ├───service       # 비즈니스 로직 계층
│       │   └───user          # 사용자 도메인 관련 코드
│       └───test                      # 테스트 코드 디렉토리
├───README.md                        # 프로젝트 소개 및 설명 파일

```


<br/>

## 👩🏻‍💻 팀원

|                      **gns1719**                      |                      **llHyun**                      |                      **Junghyeongjun**                      |
|:-----------------------------------------------------:|:----------------------------------------------------:|:-----------------------------------------------------------:|
| <img src="https://github.com/gns1719.png" width="80"> | <img src="https://github.com/llHyun.png" width="80"> | <img src="https://github.com/Junghyeongjun.png" width="80"> |
|           [김회훈](https://github.com/gns1719)           |           [이도현](https://github.com/llHyun)           |           [정형준](https://github.com/Junghyeongjun)           |


## 💁‍♂️ Detail Role
+ [김회훈](https://github.com/gns1719) / Back-end & Front-end 
  - Spring Boot 기반 반려동물 인증 백엔드 시스템 개발
  - Redis를 활용한 JWT 리프레시 토큰 저장 및 인증 처리 구현
  - 사용자 인증 및 보안 로직 (JWT 발급, 갱신, 로그아웃 등) 개발
  - Flutter 앱과의 통신을 위한 REST API 설계 및 구현
  - Spring REST Docs를 활용한 API 문서 자동화 및 정리

  <!-- 
    예시 입니다. 각자 기능 구현 후 하나 씩 추가해주세요.
  
  - 그룹, 게시글, 클래스, 쿠폰, 리뷰, 문의, 결제 구현
  - DB 클래스 정보를 Elasticsearch로 옮기는 배치 작업 및 Elasticsearch 구현
  - DB 이중화 구성  
  - Swagger, REST docs 문서화
    -->
<Br>

+ [이도현](https://github.com/llHyun) / Back-end & Front-end
  - Spring Boot 기반 반려동물 인증 백엔드 시스템 개발
  - 사용자-반려동물 관계 기반 도메인 및 DB 구조 설계 및 구현
  - AI 연동 특성을 반영한 비문 데이터 구조 분리 및 처리 전략 수립
  - 서버 인증 로직을 고려한 Flutter 로그인 화면 및 동작 설계




<Br>

+ [정형준](https://github.com/Junghyeongjun) / Ai-engineer

<Br>



 <br>
=======
# Pet-NosePrint-Id-Service
>>>>>>> marster
