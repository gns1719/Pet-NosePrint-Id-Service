package com.example.pet_noseprint_id.pet.controller;

import com.example.pet_noseprint_id.pet.domain.Pet;
import com.example.pet_noseprint_id.pet.dto.AddPetRequest;
import com.example.pet_noseprint_id.pet.dto.PetInfoResponse;
import com.example.pet_noseprint_id.pet.dto.PetUpdateRequest;
import com.example.pet_noseprint_id.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;

import java.util.List;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping("/register")
    public ResponseEntity<Void> addPet(@RequestBody AddPetRequest request,
                                       Authentication authentication) {
        Long userKey = (Long) authentication.getPrincipal(); // 토큰에서 추출된 userId
        petService.addPet(request, userKey); // 유저 ID를 명시적으로 넘겨줌
        return ResponseEntity.status(HttpStatus.CREATED).build();
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
    public ResponseEntity<List<PetInfoResponse>> getMyPets(@AuthenticationPrincipal Integer userKey) {
        List<PetInfoResponse> myPets = null;
        return ResponseEntity.ok(myPets);
    }



    @GetMapping("/list")
    public ResponseEntity<List<PetInfoResponse>> getMyPets(@AuthenticationPrincipal Long userKey) {
        List<PetInfoResponse> myPets = petService.getPetsByUserKey(userKey);
        return ResponseEntity.ok(myPets);
    }


}

