package com.example.synchronyproject.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class ImgurService {
    @Value("${imgur.client-id}")
    private String imgurClientId;

    private final RestTemplate restTemplate;

    public ImgurService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String uploadImage(byte[] imageData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Client-ID " + imgurClientId);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", imageData);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String response = restTemplate.postForObject("https://api.imgur.com/3/image", requestEntity, String.class);
        return extractImageUrl(response);
    }

    public void deleteImage(String imageUrl) {
        String imageDeleteHash = extractImageDeleteHash(imageUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Client-ID " + imgurClientId);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        restTemplate.exchange("https://api.imgur.com/3/image/" + imageDeleteHash, HttpMethod.DELETE, entity, String.class);
    }

    private String extractImageDeleteHash(String imageUrl) {
        // Split the imageUrl by "/"
        String[] parts = imageUrl.split("/");

        // The last part of the URL should be the image file name
        String imageFileName = parts[parts.length - 1];

        // Remove the file extension (e.g., .jpeg, .png) from the file name
        String imageDeleteHash = imageFileName.split("\\.")[0];

        return imageDeleteHash;
    }

    private String extractImageUrl(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.get("data");
            if (dataNode != null) {
                JsonNode imageUrlNode = dataNode.get("link");
                if (imageUrlNode != null) {
                    return imageUrlNode.asText();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
