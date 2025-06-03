package com.example.pet_noseprint_id.pet.controller;

import com.example.pet_noseprint_id.pet.dto.*;
import com.example.pet_noseprint_id.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

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


    @PutMapping("/{petId}/update")
    public ResponseEntity<PetUpdateDTO> updatePetInfo(
            @PathVariable Long petId,
            @RequestBody PetUpdateDTO request,
            @AuthenticationPrincipal Integer userKey
    ) {
        Long userKeyLong = Long.valueOf(userKey);
        PetUpdateDTO updatedPet = petService.updatePet(petId, request, userKeyLong);
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

    @PostMapping("/register/nose")
    public ResponseEntity<Void> addPetNose(@RequestBody AddPetNoseRequest request,
                                           @AuthenticationPrincipal Integer userKey) {
        Long userKeyLong = Long.valueOf(userKey);
        petService.addPetNose(request, userKeyLong);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    //임시(모델 학습 시키는 코드)
    /*@PostMapping("/noseAnalysisUrl")
    public ResponseEntity<FindUserDTO> findPets(@AuthenticationPrincipal Integer userKey) {

        FindUserDTO f = null;

        return ResponseEntity.ok(f);
    }*/
}

