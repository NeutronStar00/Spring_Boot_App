package com.example.synchronyproject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ImgurService {
    @Value("${imgur.client-id}")
    private String imgurClientId;

    private final RestTemplate restTemplate;

    public ImgurService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, String> uploadImage(byte[] imageData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "Client-ID " + imgurClientId);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", imageData);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String response = restTemplate.postForObject("https://api.imgur.com/3/image", requestEntity, String.class);
        return extractImageDetails(response);
    }

    public ResponseEntity<String> deleteImage(String imageDeleteHash) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Client-ID " + imgurClientId);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        String deleteUrl = "https://api.imgur.com/3/image/" + imageDeleteHash;
        System.out.println("DELETE URL: " + deleteUrl); // Log or print the complete URL

        try {
            ResponseEntity<String> response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, requestEntity, String.class);
            System.out.println("DELETE RESPONSE: " + response.getBody()); // Log the response body
            return response;
        } catch (HttpStatusCodeException e) {
            // Handle specific HTTP status code exceptions
            System.err.println("Error deleting image: " + e.getResponseBodyAsString()); // Log the error
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            // Handle other exceptions
            System.err.println("Error deleting image: " + e.getMessage()); // Log the error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting image: " + e.getMessage());
        }
    }

    private Map<String, String> extractImageDetails(String response) {
        Map<String, String> details = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataNode = rootNode.get("data");
            if (dataNode != null) {
                JsonNode imageUrlNode = dataNode.get("link");
                JsonNode deleteHashNode = dataNode.get("deletehash");
                if (imageUrlNode != null) {
                    details.put("link", imageUrlNode.asText());
                }
                if (deleteHashNode != null) {
                    details.put("deletehash", deleteHashNode.asText());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return details;
    }
}
