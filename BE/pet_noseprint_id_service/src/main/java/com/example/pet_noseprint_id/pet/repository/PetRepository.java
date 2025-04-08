package com.example.pet_noseprint_id.pet.repository;

import com.example.pet_noseprint_id.pet.domain.Pet;
import org.springframework.data.repository.CrudRepository;


public interface PetRepository extends CrudRepository<Pet, Long> {

}
