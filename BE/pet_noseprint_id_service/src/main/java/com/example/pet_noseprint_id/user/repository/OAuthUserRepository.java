package com.example.pet_noseprint_id.user.repository;

import com.example.pet_noseprint_id.user.domain.OAuthUser;
import org.springframework.data.repository.CrudRepository;

public interface OAuthUserRepository extends CrudRepository<OAuthUser, Long> {
}

