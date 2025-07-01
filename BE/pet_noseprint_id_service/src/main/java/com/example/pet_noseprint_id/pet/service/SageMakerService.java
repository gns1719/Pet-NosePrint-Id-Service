package com.example.pet_noseprint_id.pet.service;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointRequest;
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointResponse;

import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SageMakerService {

    @Value("${aws.sagemaker.endpoint-name}")
    private String endpointName;

    private final SageMakerRuntimeClient sageMakerClient;
    private final ObjectMapper objectMapper = new ObjectMapper();



    public float[] predictFromImage(InputStream imageStream) throws Exception {
        byte[] imageBytes = IOUtils.toByteArray(imageStream);
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        InvokeEndpointRequest request = InvokeEndpointRequest.builder()
                .endpointName(endpointName)
                .contentType("application/json")
                .body(SdkBytes.fromUtf8String("{\"image_base64\": \"" + base64Image + "\"}"))
                .build();

        InvokeEndpointResponse response = sageMakerClient.invokeEndpoint(request);
        String result = response.body().asUtf8String();

        JsonNode json = objectMapper.readTree(result);
        JsonNode embeddingNode = json.get("embedding");

        // float[]로 변환
        float[] embeddingArray = objectMapper.convertValue(embeddingNode, float[].class);


        return embeddingArray;
    }
}
