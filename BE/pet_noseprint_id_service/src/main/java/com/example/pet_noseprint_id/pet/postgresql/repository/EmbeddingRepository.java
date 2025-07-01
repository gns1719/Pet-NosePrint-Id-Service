package com.example.pet_noseprint_id.pet.postgresql.repository;

import com.example.pet_noseprint_id.pet.dto.SimilarPetDTO;
import com.example.pet_noseprint_id.pet.postgresql.domain.Embedding;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EmbeddingRepository {


    private final JdbcTemplate jdbcTemplate;

    // @Qualifier로 어떤 JdbcTemplate을 쓸지 명시
    public EmbeddingRepository(@Qualifier("postgresJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void saveEmbedding(Long petId, Long userId,  float[] embedding) {

        Float[] floatBoxedArray = new Float[embedding.length];
        for (int i = 0; i < embedding.length; i++) {
            floatBoxedArray[i] = embedding[i];
        }
        String vectorStr = Arrays.stream(floatBoxedArray)
                .map(Object::toString)
                .collect(Collectors.joining(",", "[", "]"));  // 대괄호 포함

        String sql = String.format(
                "INSERT INTO dog_nose_embeddings (pet_id, user_id, embedding) VALUES (%d, %d, '%s'::vector)",
                petId, userId, vectorStr
        );

        jdbcTemplate.execute(sql);

    }
    public Optional<SimilarPetDTO> findMostSimilarPet(float[] queryEmbedding) {
        Float[] floatBoxedArray = new Float[queryEmbedding.length];
        for (int i = 0; i < queryEmbedding.length; i++) {
            floatBoxedArray[i] = queryEmbedding[i];
        }
        String vectorStr = Arrays.stream(floatBoxedArray)
                .map(Object::toString)
                .collect(Collectors.joining(",", "[", "]"));

        String sql = String.format(
                "SELECT pet_id, 1 - (embedding <=> '%s'::vector) AS similarity " +
                        "FROM dog_nose_embeddings " +
                        "ORDER BY embedding <=> '%s'::vector " +
                        "LIMIT 1",
                vectorStr, vectorStr
        );

        List<SimilarPetDTO> results = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new SimilarPetDTO(rs.getLong("pet_id"), rs.getLong("similarity"))
        );

        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.get(0));
    }
}