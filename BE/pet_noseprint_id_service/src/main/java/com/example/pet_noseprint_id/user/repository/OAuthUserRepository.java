package com.example.pet_noseprint_id.user.repository;

import com.example.pet_noseprint_id.user.domain.OAuthUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OAuthUserRepository extends CrudRepository<OAuthUser, Long> {
    Optional<OAuthUser> findByProviderAndProviderKey(String provider, String providerKey);
}

