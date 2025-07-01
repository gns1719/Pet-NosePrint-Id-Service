package com.example.pet_noseprint_id.user.repository;

import com.example.pet_noseprint_id.user.domain.OAuthUser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class OAuthUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public OAuthUserRepository(@Qualifier("mysqlJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<OAuthUser> findByProviderAndProviderKey(String provider, String providerKey) {
        String sql = "SELECT * FROM OAuth_User WHERE provider = ? AND provider_key = ?";
        List<OAuthUser> result = jdbcTemplate.query(sql,
                (rs, rowNum) -> new OAuthUser(
                        rs.getLong("oauth_key"),
                        rs.getLong("user_key"),
                        rs.getString("provider"),
                        rs.getString("provider_key")
                ),
                provider, providerKey);
        return result.stream().findFirst();
    }

    public OAuthUser save(OAuthUser oauthUser) {
        String sql = "INSERT INTO OAuth_User (user_key, provider, provider_key) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, oauthUser.getUserKey());
            ps.setString(2, oauthUser.getProvider());
            ps.setString(3, oauthUser.getProviderKey());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            oauthUser.setOauthKey(key.longValue());
            return oauthUser;
        } else {
            throw new RuntimeException("Failed to retrieve generated key for OAuthUser");
        }
    }


    // 필요하면 update, delete 메서드도 추가 가능
}
