package com.example.pet_noseprint_id.pet.repository;

import com.example.pet_noseprint_id.pet.domain.Pet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class PetRepository {

    private final JdbcTemplate jdbcTemplate;

    public PetRepository(@Qualifier("mysqlJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Pet> findByUserKey(Long userKey) {
        String sql = "SELECT pet_id, user_key, name, birth, profile, gender FROM Pet WHERE user_key = ?";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    Pet pet = new Pet();
                    pet.setPetId(rs.getLong("pet_id"));
                    pet.setUserKey(rs.getLong("user_key"));
                    pet.setName(rs.getString("name"));
                    Date birthDate = rs.getDate("birth");
                    pet.setBirth(birthDate != null ? birthDate.toLocalDate() : null);
                    pet.setProfile(rs.getString("profile"));
                    pet.setGender(rs.getString("gender"));
                    return pet;
                },
                userKey);
    }

    public boolean existsByUserKey(Long userKey) {
        String sql = "SELECT COUNT(*) FROM Pet WHERE user_key = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userKey);
        return count != null && count > 0;
    }

    public Pet save(Pet pet) {
        String sql = "INSERT INTO Pet (user_key, name, birth, profile, gender) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, pet.getUserKey());
            ps.setString(2, pet.getName());
            if (pet.getBirth() != null) {
                ps.setDate(3, Date.valueOf(pet.getBirth()));
            } else {
                ps.setDate(3, null);
            }
            ps.setString(4, pet.getProfile());
            ps.setString(5, pet.getGender());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            pet.setPetId(key.longValue());
            return pet;
        } else {
            throw new RuntimeException("Failed to retrieve generated key for Pet");
        }
    }
    public Optional<Pet> findById(Long petId) {
        String sql = "SELECT pet_id, user_key, name, birth, profile, gender FROM Pet WHERE pet_id = ?";
        List<Pet> result = jdbcTemplate.query(sql,
                (rs, rowNum) -> {
                    Pet pet = new Pet();
                    pet.setPetId(rs.getLong("pet_id"));
                    pet.setUserKey(rs.getLong("user_key"));
                    pet.setName(rs.getString("name"));
                    Date birthDate = rs.getDate("birth");
                    pet.setBirth(birthDate != null ? birthDate.toLocalDate() : null);
                    pet.setProfile(rs.getString("profile"));
                    pet.setGender(rs.getString("gender"));
                    return pet;
                },
                petId);
        return result.stream().findFirst();
    }

    public int update(Pet pet) {
        String sql = "UPDATE Pet SET user_key = ?, name = ?, birth = ?, profile = ?, gender = ? WHERE pet_id = ?";

        return jdbcTemplate.update(sql,
                pet.getUserKey(),
                pet.getName(),
                pet.getBirth(),    // LocalDate는 JdbcTemplate이 알아서 변환해줍니다.
                pet.getProfile(),
                pet.getGender(),
                pet.getPetId()
        );
    }
}