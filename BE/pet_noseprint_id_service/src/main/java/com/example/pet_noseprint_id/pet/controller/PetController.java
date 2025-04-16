package com.example.pet_noseprint_id.pet.controller;

import com.example.pet_noseprint_id.pet.dto.AddPetRequest;
import com.example.pet_noseprint_id.pet.dto.PetInfoResponse;
import com.example.pet_noseprint_id.pet.dto.PetUpdateRequest;
import com.example.pet_noseprint_id.pet.dto.UpdateProfileUrlRequest;
import com.example.pet_noseprint_id.pet.service.AwsService;
import com.example.pet_noseprint_id.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    /*@PostMapping("/register")
    public ResponseEntity<Void> addPet(@RequestBody AddPetRequest request,
                                       @AuthenticationPrincipal Integer userKey) {
        Long userKeyLong = Long.valueOf(userKey);
        petService.addPet(request, userKeyLong); // 유저 ID를 명시적으로 넘겨줌
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }*/
    @PostMapping("/register")
    public ResponseEntity<Long> addPet(@RequestBody AddPetRequest request,
                                       @AuthenticationPrincipal Integer userKey) {
        Long userKeyLong = Long.valueOf(userKey);
        Long petId = petService.addPet(request, userKeyLong);  // petId 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(petId);
    }

    @PatchMapping("/{petId}/profile-url")
    public ResponseEntity<Void> updatePetProfileUrl(@PathVariable Long petId,
                                                    @RequestBody UpdateProfileUrlRequest request,
                                                    @AuthenticationPrincipal Integer userKey) {
        Long userKeyLong = Long.valueOf(userKey);
        petService.updateProfileUrl(petId, userKeyLong, request.getProfile());
        return ResponseEntity.ok().build();
    }


    @PutMapping("/{petId}")
    public ResponseEntity<PetInfoResponse> updatePetInfo(
            @PathVariable Long petId,
            @RequestBody PetUpdateRequest request,
            Authentication authentication // Authentication 추가
    ) {
        Long userKey = (Long) authentication.getPrincipal(); // AccessToken에서 userId 추출
        PetInfoResponse updatedPet = petService.updatePet(petId, request, userKey);
        return ResponseEntity.ok(updatedPet);
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getMyPets(@AuthenticationPrincipal Integer userKey) {
        List<PetInfoResponse> myPets = petService.getPetsByUserKey(userKey);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", myPets.isEmpty() ? "등록된 반려동물이 없습니다" : "반려동물 목록 조회 성공");
        response.put("data", myPets);

        return ResponseEntity.ok(response);
    }
}

