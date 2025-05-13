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
<<<<<<< HEAD
                                       @AuthenticationPrincipal Long userKey) {


        Long petId = petService.addPet(request, userKey);  // petId 반환
=======
                                       @AuthenticationPrincipal Integer userKey) {
        Long userKeyLong = Long.valueOf(userKey);
        Long petId = petService.addPet(request, userKeyLong);  // petId 반환
>>>>>>> baa1f6724f3798da5682ca57f49d147768a61a4b
        return ResponseEntity.status(HttpStatus.CREATED).body(petId);
    }

    @PatchMapping("/{petId}/profile-url")
    public ResponseEntity<Void> updatePetProfileUrl(@PathVariable Long petId,
                                                    @RequestBody UpdateProfileUrlRequest request,
<<<<<<< HEAD
                                                    @AuthenticationPrincipal Long userKey) {
        petService.updateProfileUrl(petId, userKey, request.getProfile());
=======
                                                    @AuthenticationPrincipal Integer userKey) {
        Long userKeyLong = Long.valueOf(userKey);
        petService.updateProfileUrl(petId, userKeyLong, request.getProfile());
>>>>>>> baa1f6724f3798da5682ca57f49d147768a61a4b
        return ResponseEntity.ok().build();
    }


    @PutMapping("/{petId}/update")
    public ResponseEntity<PetUpdateDTO> updatePetInfo(
            @PathVariable Long petId,
            @RequestBody PetUpdateDTO request,
<<<<<<< HEAD
            @AuthenticationPrincipal Long userKey
    ) {
        PetUpdateDTO updatedPet = petService.updatePet(petId, request, userKey);
=======
            @AuthenticationPrincipal Integer userKey
    ) {
        Long userKeyLong = Long.valueOf(userKey);
        PetUpdateDTO updatedPet = petService.updatePet(petId, request, userKeyLong);
>>>>>>> baa1f6724f3798da5682ca57f49d147768a61a4b
        return ResponseEntity.ok(updatedPet);
    }

    @GetMapping("/list")
<<<<<<< HEAD
    public ResponseEntity<Map<String, Object>> getMyPets(@AuthenticationPrincipal Long userKey) {
=======
    public ResponseEntity<Map<String, Object>> getMyPets(@AuthenticationPrincipal Integer userKey) {
>>>>>>> baa1f6724f3798da5682ca57f49d147768a61a4b
        List<PetInfoResponse> myPets = petService.getPetsByUserKey(userKey);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", myPets.isEmpty() ? "등록된 반려동물이 없습니다" : "반려동물 목록 조회 성공");
        response.put("data", myPets);

        return ResponseEntity.ok(response);
    }
<<<<<<< HEAD
}
=======
}

>>>>>>> baa1f6724f3798da5682ca57f49d147768a61a4b
