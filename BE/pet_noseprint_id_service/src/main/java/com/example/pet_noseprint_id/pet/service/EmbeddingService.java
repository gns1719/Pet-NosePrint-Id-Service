package com.example.pet_noseprint_id.pet.service;

import com.example.pet_noseprint_id.pet.dto.SimilarPetDTO;
import com.example.pet_noseprint_id.pet.postgresql.domain.Embedding;
import com.example.pet_noseprint_id.pet.postgresql.repository.EmbeddingRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class EmbeddingService {

    private final EmbeddingRepository embeddingRepository;

    public EmbeddingService(EmbeddingRepository dogNoseEmbeddingRepository) {
        this.embeddingRepository = dogNoseEmbeddingRepository;
    }

    public void saveEmbedding(Long petId, Long userId,  float[] embedding) {
        //Embedding entity = new Embedding(petId, userId, embedding);
        embeddingRepository.saveEmbedding(petId, userId, embedding);
    }

    public Optional<SimilarPetDTO> findSimilarPets(float[] queryEmbedding) {
        return embeddingRepository.findMostSimilarPet(queryEmbedding);
    }
}