package com.example.pet_noseprint_id.pet.repository;

import com.example.pet_noseprint_id.pet.domain.Pet;
import org.springframework.data.repository.CrudRepository;
import java.util.List;



public interface PetRepository extends CrudRepository<Pet, Long> {
    List<Pet> findByUserKey(Long userKey);
    boolean existsByUserKey(Long userKey);
}
