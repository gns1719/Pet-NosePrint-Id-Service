package com.example.pet_noseprint_id.pet.repository;

import com.example.pet_noseprint_id.pet.domain.Pet;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

@Table("Pet")
public interface PetRepository extends CrudRepository<Pet, Long> {
<<<<<<< HEAD
    List<Pet> findByUserKey(Long userKey);
    boolean existsByUserKey(Long userKey);
=======
    List<Pet> findByUserKey(Integer userKey);
    boolean existsByUserKey(Integer userKey);
>>>>>>> baa1f6724f3798da5682ca57f49d147768a61a4b
}
