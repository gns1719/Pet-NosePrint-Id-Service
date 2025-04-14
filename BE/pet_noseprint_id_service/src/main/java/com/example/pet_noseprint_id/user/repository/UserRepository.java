package com.example.pet_noseprint_id.user.repository;

import com.example.pet_noseprint_id.user.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findById(Long id);

    boolean existsByEmail(String email);
}