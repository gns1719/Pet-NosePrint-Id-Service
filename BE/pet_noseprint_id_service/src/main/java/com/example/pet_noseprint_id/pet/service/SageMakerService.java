package com.example.pet_noseprint_id.pet.service;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class SageMakerService {

    @Value("${aws.sagemaker.endpoint-name}")
    private String endpointName;

    private final SageMakerRuntimeClient runtimeClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SageMakerService() {
        this.runtimeClient = SageMakerRuntimeClient.builder()
                .region(Region.AP_NORTHEAST_2)
                .build();
    }

    public float[] predictFromImage(InputStream imageStream) throws Exception {
        byte[] imageBytes = IOUtils.toByteArray(imageStream);
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        InvokeEndpointRequest request = InvokeEndpointRequest.builder()
                .endpointName(endpointName)
                .contentType("application/json")
                .body(SdkBytes.fromUtf8String("{\"image_base64\": \"" + base64Image + "\"}"))
                .build();

        InvokeEndpointResponse response = runtimeClient.invokeEndpoint(request);
        String result = response.body().asUtf8String();

        JsonNode json = objectMapper.readTree(result);
        JsonNode embeddingNode = json.get("embedding");

        return objectMapper.convertValue(embeddingNode, float[].class);
    }
}
