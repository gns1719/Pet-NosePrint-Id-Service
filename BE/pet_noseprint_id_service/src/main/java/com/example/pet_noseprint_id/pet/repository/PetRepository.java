package com.example.pet_noseprint_id.pet.repository;

import com.example.pet_noseprint_id.pet.domain.Pet;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;


public interface PetRepository extends CrudRepository<Pet, Long> {
    List<Pet> findByUserKey(Integer userKey);
    boolean existsByUserKey(Integer userKey);
}
