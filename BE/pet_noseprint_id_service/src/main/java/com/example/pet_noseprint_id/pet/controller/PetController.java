package com.example.pet_noseprint_id.pet.controller;

import com.example.pet_noseprint_id.pet.dto.AddPetRequest;
import com.example.pet_noseprint_id.pet.dto.PetInfoResponse;
import com.example.pet_noseprint_id.pet.dto.PetUpdateDTO;
import com.example.pet_noseprint_id.pet.dto.UpdateProfileUrlRequest;
import com.example.pet_noseprint_id.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
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
    public ResponseEntity<PetUpdateDTO> updatePetInfo(
            @PathVariable Long petId,
            @RequestBody PetUpdateDTO request,
            @AuthenticationPrincipal Long userKey
    ) {
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
}