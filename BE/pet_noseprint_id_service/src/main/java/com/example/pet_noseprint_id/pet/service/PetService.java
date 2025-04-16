package com.example.pet_noseprint_id.pet.service;


import com.example.pet_noseprint_id.pet.domain.Pet;
import com.example.pet_noseprint_id.pet.dto.AddPetRequest;
import com.example.pet_noseprint_id.pet.dto.PetInfoResponse;
import com.example.pet_noseprint_id.pet.dto.PetUpdateRequest;
import com.example.pet_noseprint_id.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    public void addPet(AddPetRequest request, Long userKey) {
        Pet pet = new Pet(
                null, // petId는 DB에서 auto-increment
                userKey,
                request.getName(),
                request.getBirth(),
                request.getGender(),
                request.getProfile()
        );

        petRepository.save(pet);
    }

    public PetInfoResponse updatePet(Long petId, PetUpdateRequest request, Long userKey) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        if (!pet.getUserKey().equals(userKey)) {
            throw new AccessDeniedException("해당 펫을 수정할 권한이 없습니다.");
        }

        pet.setName(request.getName());
        pet.setBirth(request.getBirth());
        pet.setGender(request.getGender());
        pet.setProfile(request.getProfile());

        petRepository.save(pet);

        return new PetInfoResponse(
                pet.getPetId(),
                pet.getName(),
                pet.getBirth(),
                pet.getGender(),
                pet.getProfile()
        );
    }

    /*public List<Pet> getPetsByUserId(String userId) {
        return petRepository.findByUserId(userId);
    }*/

    public List<PetInfoResponse> getPetsByUserKey(Long userKey) {
        List<Pet> pets = petRepository.findByUserKey(userKey);
        return pets.stream()
                .map(pet -> new PetInfoResponse(
                        pet.getPetId(),
                        pet.getName(),
                        pet.getBirth(),
                        pet.getGender(),
                        pet.getProfile()
                ))
                .collect(Collectors.toList());
    }


}
