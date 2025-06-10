package com.example.pet_noseprint_id.pet.controller;

import com.example.pet_noseprint_id.pet.dto.*;
import com.example.pet_noseprint_id.pet.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    @Value("${aws.bucket.name}")
    private String bucketName;

    private final PetService petService;
    private final S3Service s3Service;
    private final SageMakerService sageMakerService;
    private final EmbeddingService embeddingService;

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
        String imgFileName = "noseSave/"+petId + "-" + userKey + ".jpg";

        try (InputStream inputStream = s3Service.downloadImage(bucketName, imgFileName)) {
            float[] embedding = sageMakerService.predictFromImage(inputStream);
            embeddingService.saveEmbedding(Long.valueOf(petId), userKey, embedding);
            s3Service.deleteImage(bucketName, imgFileName);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            // 로그 처리 및 예외 응답
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
