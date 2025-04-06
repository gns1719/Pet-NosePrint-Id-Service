package com.example.pet_noseprint_id.pet.controller;

import com.example.pet_noseprint_id.pet.dto.AddPetRequest;
import com.example.pet_noseprint_id.pet.dto.PetInfoResponse;
import com.example.pet_noseprint_id.pet.dto.PetUpdateRequest;
import com.example.pet_noseprint_id.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping
    public ResponseEntity<Void> addPet(@RequestBody AddPetRequest request) {
        petService.addPet(request.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{petId}")
    public ResponseEntity<PetInfoResponse> updatePetInfo(
            @PathVariable Long petId,
            @RequestBody PetUpdateRequest request
    ) {
        PetInfoResponse updatedPet = petService.updatePet(petId, request);
        return ResponseEntity.ok(updatedPet);
    }


}

