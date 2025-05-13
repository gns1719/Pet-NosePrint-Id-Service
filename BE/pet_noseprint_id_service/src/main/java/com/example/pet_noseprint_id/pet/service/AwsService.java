package com.example.pet_noseprint_id.pet.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import java.time.Duration;

<<<<<<< HEAD
import static java.time.Duration.ofMinutes;

=======
>>>>>>> baa1f6724f3798da5682ca57f49d147768a61a4b
@Service
public class AwsService {

    private final S3Presigner s3Presigner;
    private final String bucketName;

    public AwsService(@Value("${aws.access.key}") String accessKey,
                      @Value("${aws.secret.key}") String secretKey,
                      @Value("${aws.region}") String region,
                      @Value("${aws.bucket.name}") String bucketName) {

        this.bucketName = bucketName;

        this.s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    public String generatePresignedPutUrl(String fileName) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType("image/jpeg")
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
<<<<<<< HEAD
                .signatureDuration(ofMinutes(10))
=======
                .signatureDuration(Duration.ofMinutes(10))
>>>>>>> baa1f6724f3798da5682ca57f49d147768a61a4b
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> baa1f6724f3798da5682ca57f49d147768a61a4b
