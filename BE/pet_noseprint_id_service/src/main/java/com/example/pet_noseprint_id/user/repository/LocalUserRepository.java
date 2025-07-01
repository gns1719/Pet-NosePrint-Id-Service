package com.example.pet_noseprint_id.user.repository;

import com.example.pet_noseprint_id.user.domain.LocalUser;
import com.example.pet_noseprint_id.user.domain.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class LocalUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public LocalUserRepository(@Qualifier("mysqlJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<LocalUser> findById(String id) {
        String sql = "SELECT * FROM Local_User WHERE id = ?";
        List<LocalUser> result = jdbcTemplate.query(sql,
                (rs, rowNum) -> new LocalUser(
                        rs.getLong("local_key"),
                        rs.getLong("user_key"),
                        rs.getString("id"),
                        rs.getString("password")
                ),
                id);
        return result.stream().findFirst();
    }

    public Optional<LocalUser> findByUserKey(Long userKey) {
        String sql = "SELECT * FROM Local_User WHERE user_key = ?";
        List<LocalUser> result = jdbcTemplate.query(sql,
                (rs, rowNum) -> new LocalUser(
                        rs.getLong("local_key"),
                        rs.getLong("user_key"),
                        rs.getString("id"),
                        rs.getString("password")
                ),
                userKey);
        return result.stream().findFirst();
    }

    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM Local_User WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    public void updatePassword(Long userKey, String encodedPassword) {
        String sql = "UPDATE Local_User SET password = ? WHERE user_key = ?";
//        jdbcTemplate.update(sql, encodedPassword, userKey);
    }

    public LocalUser save(LocalUser localUser) {
        String sql = "INSERT INTO Local_User (user_key, id, password) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, localUser.getUserKey());
            ps.setString(2, localUser.getId());
            ps.setString(3, localUser.getPassword());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            // 자동 생성된 PK를 localUser 객체에 세팅
            localUser.setLocalKey(key.longValue());
            return localUser;
        } else {
            throw new RuntimeException("Failed to retrieve generated key for LocalUser");
        }
    }

}
