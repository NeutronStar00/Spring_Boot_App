package com.example.synchronyproject.controller;

import com.example.synchronyproject.model.Image;
import com.example.synchronyproject.model.User;
import com.example.synchronyproject.service.ImgurService;
import com.example.synchronyproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImgurController {
    @Autowired
    private ImgurService imgurService;

    @Autowired
    private UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image, Principal principal) {
        try {
            byte[] imageData = image.getBytes();
            Map<String, String> imgurResponse = imgurService.uploadImage(imageData);
            String imageUrl = imgurResponse.get("link");
            String imageDeleteHash = imgurResponse.get("deletehash");

            User user = userService.findByUsername(principal.getName());
            userService.addUserImage(user.getId(), imageUrl, imageDeleteHash);

            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageId, Principal principal) {
        try {
            User user = userService.findByUsername(principal.getName());
            Image image = userService.findImageById(imageId);

            if (image != null && image.getUser().getId().equals(user.getId())) {
                String imageDeleteHash = image.getImageDeleteHash();
                ResponseEntity<String> deleteResponse = imgurService.deleteImage(imageDeleteHash);

                if (deleteResponse.getStatusCode().is2xxSuccessful()) {
                    userService.removeUserImage(user.getId(), imageId);
                    return ResponseEntity.ok().build();
                } else {
                    // Handle error response from Imgur API
                    return ResponseEntity.status(deleteResponse.getStatusCode()).body(deleteResponse.getBody());
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
