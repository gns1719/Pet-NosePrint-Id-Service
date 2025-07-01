package com.example.pet_noseprint_id.pet.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "dog_nose_embeddings")
public class Embedding {
    @Id
    private Long petId;

    private Long userId;

    private Float[] embedding;
}
