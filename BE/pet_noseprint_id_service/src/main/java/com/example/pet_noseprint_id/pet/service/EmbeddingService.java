package com.example.pet_noseprint_id.pet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.postgresql.util.PGobject;

import java.util.Arrays;

@Service
public class EmbeddingService {

    @Autowired
    @Qualifier("postgresJdbcTemplate")
    private JdbcTemplate jdbcTemplate;



    public void saveEmbedding(Long petId, Long userId, float[] embedding) {
        String vectorStr = Arrays.toString(embedding)
                .replace("[", "").replace("]", "");// 벡터 문자열: "0.1, 0.2, 0.3"

        try {
            PGobject pgVector = new PGobject();
            pgVector.setType("vector"); // PostgreSQL의 pgvector 타입
            pgVector.setValue(vectorStr); // 예: "0.1, 0.2, 0.3"

            jdbcTemplate.update(
                    "INSERT INTO dog_nose_embeddings (pet_id, user_id, embedding) VALUES (?, ?, ?)",
                    petId, userId, pgVector
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to save embedding", e);
        }
    }
}
