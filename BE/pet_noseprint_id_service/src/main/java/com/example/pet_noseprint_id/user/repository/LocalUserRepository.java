package com.example.pet_noseprint_id.user.repository;

import com.example.pet_noseprint_id.user.domain.LocalUser;
import com.example.pet_noseprint_id.user.domain.User;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Table("Local_User")
public interface LocalUserRepository extends CrudRepository<LocalUser, Long> {
    Optional<LocalUser> findById(String id);
    Optional<LocalUser> findByUserKey(Long userKey);

    boolean existsById(String id);
    @Modifying
    @Query("UPDATE Local_User SET password = :encodedPassword WHERE user_key = :userKey")
    void updatePassword(@Param("userKey") Long userKey, @Param("encodedPassword") String encodedPassword);
}
