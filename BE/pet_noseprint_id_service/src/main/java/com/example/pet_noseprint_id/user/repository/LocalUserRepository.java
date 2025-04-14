package com.example.pet_noseprint_id.user.repository;

import com.example.pet_noseprint_id.user.domain.LocalUser;
import com.example.pet_noseprint_id.user.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface LocalUserRepository extends CrudRepository<LocalUser, Long> {
    Optional<LocalUser> findById(String id);

    boolean existsById(String id);
}
