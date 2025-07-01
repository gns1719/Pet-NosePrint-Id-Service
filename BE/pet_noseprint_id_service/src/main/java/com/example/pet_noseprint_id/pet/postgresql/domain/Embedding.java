package com.example.pet_noseprint_id.pet.postgresql.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Embedding {
    @Id
    private Long petId;

    private Long userId;

    @Column(name = "embedding", columnDefinition = "vector")
    private String  embedding;
}
