package com.example.pet_noseprint_id.pet.controller;

import com.example.pet_noseprint_id.pet.service.AwsService;
import com.example.pet_noseprint_id.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final AwsService awsService;
    private final PetService petService;


    @GetMapping("/presigned-url")
    public ResponseEntity<String> getPresignedUrl(@RequestParam String fileName, @AuthenticationPrincipal Long userKey) {
        if(petService.userExists(userKey)) {
            String fullFileName = userKey + "-" + fileName;
            String presignedUrl = awsService.generatePresignedPutUrl(fullFileName);
            return ResponseEntity.ok(presignedUrl);
        }
        throw new IllegalArgumentException("유효하지 않은 유저입니다.");
    }


}