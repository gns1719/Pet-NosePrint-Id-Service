package com.example.pet_noseprint_id.pet.service;


import com.example.pet_noseprint_id.pet.domain.Pet;
import com.example.pet_noseprint_id.pet.dto.AddPetRequest;
import com.example.pet_noseprint_id.pet.dto.PetInfoResponse;
import com.example.pet_noseprint_id.pet.dto.PetUpdateRequest;
import com.example.pet_noseprint_id.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    public void addPet(Long userId, AddPetRequest request) {
        Pet pet = new Pet(
                null, // petId는 DB에서 auto-increment
                userId,
                request.getName(),
                request.getBirth(),
                request.getGender(),
                request.getProfile()
        );

        petRepository.save(pet);
    }

    public PetInfoResponse updatePet(Long petId, PetUpdateRequest request) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));

        pet.setName(request.getName());
        pet.setBirth(request.getBirth());
        pet.setGender(request.getGender());
        pet.setProfile(request.getProfile());

        petRepository.save(pet);

        return new PetInfoResponse(
                pet.getName(),
                pet.getBirth(),
                pet.getGender(),
                pet.getProfile()
        );
    }
}
