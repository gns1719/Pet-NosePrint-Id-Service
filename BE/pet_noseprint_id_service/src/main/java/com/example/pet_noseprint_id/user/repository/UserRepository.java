package com.example.pet_noseprint_id.user.repository;

import com.example.pet_noseprint_id.user.domain.User;
import com.example.pet_noseprint_id.user.dto.UserInfoResDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {


    public final JdbcTemplate jdbcTemplate;

    public UserRepository(@Qualifier("mysqlJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findByUserKey(Long userKey) {
        String sql = "SELECT * FROM User WHERE user_key = ?";
        List<User> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), userKey);
        return result.stream().findFirst();
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM User WHERE email = ?";
        List<User> result = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), email);
        return result.stream().findFirst();
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM User WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
    public User save(User user) {
        String sql = "INSERT INTO User (login_type, name, phone_number, create_date, email) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getLoginType());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPhoneNumber());
            ps.setObject(4, user.getCreateDate());
            ps.setString(5, user.getEmail());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            user.setUserKey(key.longValue());
            return user;
        } else {
            throw new RuntimeException("Failed to retrieve generated key for User");
        }
    }


    public Optional<UserInfoResDTO> findUserInfoByUserKey(Long userKey) {
        String sql = "SELECT name, phone_number, email, login_type FROM User WHERE user_key = ?";

        try {
            UserInfoResDTO dto = jdbcTemplate.queryForObject(sql, new Object[]{userKey}, (rs, rowNum) -> {
                return new UserInfoResDTO(
                        rs.getString("name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("login_type")
                );
            });
            return Optional.ofNullable(dto);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // UserInfoResDTO 조회 등 필요한 쿼리도 직접 작성
}