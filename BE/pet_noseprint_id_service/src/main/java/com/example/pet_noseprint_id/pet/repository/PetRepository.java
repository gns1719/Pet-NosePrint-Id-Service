package com.example.pet_noseprint_id.pet.repository;

import com.example.pet_noseprint_id.pet.domain.Pet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends CrudRepository<Pet, Long> {
    List<Pet> findByUserKey(Long userKey);
    boolean existsByUserKey(Long userKey);
}
