package com.example.pet_noseprint_id.pet.controller;

import com.example.pet_noseprint_id.pet.domain.Pet;
import com.example.pet_noseprint_id.pet.dto.*;
import com.example.pet_noseprint_id.pet.repository.PetRepository;
import com.example.pet_noseprint_id.pet.service.*;
import com.example.pet_noseprint_id.user.domain.User;
import com.example.pet_noseprint_id.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {
    private static final Logger logger = LoggerFactory.getLogger(PetController.class);


    @Value("${aws.bucket.name}")
    private String bucketName;

    private final PetService petService;
    private final S3Service s3Service;
    private final SageMakerService sageMakerService;
    private final EmbeddingService embeddingService;
    private final UserRepository userService;

    @PostMapping("/register")
    public ResponseEntity<Long> addPet(@RequestBody AddPetRequest request,
                                       @AuthenticationPrincipal Long userKey) {
        Long petId = petService.addPet(request, userKey);  // petId 반환

        return ResponseEntity.status(HttpStatus.CREATED).body(petId);
    }

    @PatchMapping("/{petId}/profile-url")
    public ResponseEntity<Void> updatePetProfileUrl(@PathVariable Long petId,
                                                    @RequestBody UpdateProfileUrlRequest request,
                                                    @AuthenticationPrincipal Long userKey) {
        petService.updateProfileUrl(petId, userKey, request.getProfile());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{petId}/update")
    public ResponseEntity<PetUpdateDTO> updatePetInfo(@PathVariable Long petId,
                                                      @RequestBody PetUpdateDTO request,
                                                      @AuthenticationPrincipal Long userKey) {
        PetUpdateDTO updatedPet = petService.updatePet(petId, request, userKey);
        return ResponseEntity.ok(updatedPet);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getMyPets(@AuthenticationPrincipal Long userKey) {
        List<PetInfoResponse> myPets = petService.getPetsByUserKey(userKey);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", myPets.isEmpty() ? "등록된 반려동물이 없습니다" : "반려동물 목록 조회 성공");
        response.put("data", myPets);

        return ResponseEntity.ok(response);
    }

    // 비문 이미지 업로드 후 처리 로직
    @PostMapping("/noseSaveSuccess")
    public ResponseEntity<Void> handleNoseSaveSuccess(@RequestParam String petId,
                                                      @AuthenticationPrincipal Long userKey) {
        String imgFileName = "noseSave/" + userKey + "-" + petId + "-" + "정면" ;
        logger.info("Start processing nose save success. imgFileName={}", imgFileName);

        try (InputStream inputStream = s3Service.downloadImage(bucketName, imgFileName)) {
            logger.info("Downloaded image from S3: {}", imgFileName);

            float[] embedding = sageMakerService.predictFromImage(inputStream);
            logger.info("Received embedding vector from SageMaker, length={}", embedding.length);

            embeddingService.saveEmbedding(Long.valueOf(petId), userKey, embedding);
            logger.info("Saved embedding vector to DB for petId={}, userKey={}", petId, userKey);

            s3Service.deleteImage(bucketName, imgFileName);
            logger.info("Deleted image from S3: {}", imgFileName);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Failed to process nose save success for petId={}, userKey={}", petId, userKey, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/noseAnalysisUrl")
    public ResponseEntity<?> handleNoseSearch(@AuthenticationPrincipal Long userKey) {
        String imgFileName = "noseSearch/" + userKey;

        logger.info("[비문 분석 요청] 사용자 ID: {}", userKey);
        logger.info("[S3 다운로드] 파일명: {}", imgFileName);

        try (InputStream inputStream = s3Service.downloadImage(bucketName, imgFileName)) {

            logger.info("[임베딩 추론 시작] SageMaker 호출");
            float[] queryEmbedding = sageMakerService.predictFromImage(inputStream);
            logger.info("[임베딩 추론 완료] 벡터 길이: {}", queryEmbedding.length);

            logger.info("[유사 강아지 검색] 데이터베이스에서 코사인 유사도 비교 시작");
            Optional<SimilarPetDTO> similarPets = embeddingService.findSimilarPets(queryEmbedding);

            if (similarPets.isEmpty()) {
                logger.warn("[검색 결과 없음] 유사한 강아지를 찾을 수 없음");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유사한 강아지를 찾을 수 없습니다.");
            }

            Long matchedPetId = similarPets.get().getPetId();
            logger.info("[유사 강아지 ID] {}", matchedPetId);

            logger.info("[강아지 정보 조회] petId 기반으로 정보 조회");
            Optional<Pet> petInfos = petService.findPetInfoByPetId(matchedPetId);

            if (petInfos.isEmpty()) {
                logger.warn("[강아지 정보 없음] petId: {}", matchedPetId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("강아지 정보가 존재하지 않습니다.");
            }

            Pet pet = petInfos.get();
            logger.info("[S3 이미지 삭제] 파일명: {}", imgFileName);
            s3Service.deleteImage(bucketName, imgFileName);

            // 📌 DTO로 변환
            SearchPetResDTO dto = new SearchPetResDTO();
            dto.setName(pet.getName());
            dto.setBirth(pet.getBirth());
            dto.setGender(pet.getGender());

            // 📌 주인 전화번호 조회 (예: userService 등에서 구현되어야 함)
            Optional<User> user = userService.findByUserKey(pet.getUserKey());
            if (user.isPresent()) {
                dto.setPhoneNumber(user.get().getPhoneNumber());
            } else {
                logger.warn("[사용자 정보 없음] userKey: {}", pet.getUserKey());
                dto.setPhoneNumber("정보 없음");
            }

            logger.info("[분석 완료] 결과 DTO 반환: {}", dto);
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            logger.error("[비문 분석 실패] 에러 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류가 발생했습니다.");
        }
    }


}
