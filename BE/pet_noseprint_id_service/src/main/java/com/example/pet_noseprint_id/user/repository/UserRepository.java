package com.example.pet_noseprint_id.user.repository;

import com.example.pet_noseprint_id.user.domain.User;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Table("User")
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUserKey(Long userKey);
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}