package com.example.pet_noseprint_id.user.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table("User")
public class User {

    @Id
    private Long userKey;

    private String loginType;
    private String name;
    private String phoneNumber;
    private LocalDate createDate;
    private String email;
}