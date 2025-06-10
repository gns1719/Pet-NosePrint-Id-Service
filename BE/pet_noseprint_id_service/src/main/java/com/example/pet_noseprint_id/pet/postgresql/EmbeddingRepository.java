package com.example.pet_noseprint_id.pet.postgresql;

import com.example.pet_noseprint_id.pet.domain.Embedding;
import org.springframework.data.repository.CrudRepository;

public interface EmbeddingRepository extends CrudRepository<Embedding, Long> {

}
