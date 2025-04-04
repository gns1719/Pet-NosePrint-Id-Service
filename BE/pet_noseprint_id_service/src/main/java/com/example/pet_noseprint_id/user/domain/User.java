package com.example.pet_noseprint_id.user.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Getter
@Table("User")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity{

    @Id
    private Long userId;
    private String loginType;
    private String name;
    private String phoneNumber;
    private LocalDate createDate;
    private String email;

}
