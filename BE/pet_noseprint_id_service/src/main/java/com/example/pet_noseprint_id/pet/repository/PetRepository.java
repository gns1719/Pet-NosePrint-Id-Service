package com.example.pet_noseprint_id.pet.repository;

import com.example.pet_noseprint_id.pet.domain.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

}
