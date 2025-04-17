package com.example.pet_noseprint_id.pet.service;


import com.example.pet_noseprint_id.pet.domain.Pet;
import com.example.pet_noseprint_id.pet.dto.AddPetRequest;
import com.example.pet_noseprint_id.pet.dto.PetInfoResponse;
import com.example.pet_noseprint_id.pet.dto.PetUpdateDTO;
import com.example.pet_noseprint_id.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    /*public void addPet(AddPetRequest request, Long userKey) {
        Pet pet = new Pet(
                null, // petId는 DB에서 auto-increment
                userKey,
                request.getName(),
                request.getBirth(),
                null,   //profile url 은 리턴한 후 등록
                request.getGender()
        );

        petRepository.save(pet);
    }*/
    public Long addPet(AddPetRequest request, Long userKey) {
        Pet pet = new Pet(
                null,
                userKey,
                request.getName(),
                request.getBirth(),
                null, // profile URL은 아직 없음
                request.getGender()
        );

        Pet saved = petRepository.save(pet); // 저장된 petId 반환
        return saved.getPetId();
    }

    public void updateProfileUrl(Long petId, Long userKey, String profileUrl) {
        Optional<Pet> optionalPet = petRepository.findById(petId);
        Pet pet = optionalPet.orElseThrow(() -> new IllegalArgumentException("해당 펫이 존재하지 않습니다."));

        // userKey 소유자 맞는지 검증
        if (!pet.getUserKey().equals(userKey)) {
            throw new SecurityException("해당 펫에 접근 권한이 없습니다.");
        }

        // 프로필 URL 업데이트
        Pet updated = new Pet(
                pet.getPetId(),
                pet.getUserKey(),
                pet.getName(),
                pet.getBirth(),
                profileUrl,
                pet.getGender()
        );

        petRepository.save(updated);
    }


    public PetUpdateDTO updatePet(Long petId, PetUpdateDTO request, Long userKey) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        if (!pet.getUserKey().equals(userKey)) {
            throw new AccessDeniedException("해당 펫을 수정할 권한이 없습니다.");
        }

        pet.setName(request.getName());
        pet.setBirth(request.getBirth());
        pet.setGender(request.getGender());

        petRepository.save(pet);

        return new PetUpdateDTO(
                pet.getName(),
                pet.getBirth(),
                pet.getGender()
        );
    }

    /*public List<Pet> getPetsByUserId(String userId) {
        return petRepository.findByUserId(userId);
    }*/

    public List<PetInfoResponse> getPetsByUserKey(Integer userKey) {
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


    public boolean userExists(Integer userKey) {
        return petRepository.existsByUserKey(userKey);
    }

}
