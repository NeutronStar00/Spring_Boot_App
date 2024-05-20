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
            String imageUrl = imgurService.uploadImage(imageData);
            User user = userService.findByUsername(principal.getName());
            userService.addUserImage(user.getId(), imageUrl);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId, Principal principal) {
        try {
            User user = userService.findByUsername(principal.getName());
            Image image = userService.findImageById(imageId);
            if (image != null) {
                String imageUrl = image.getImageUrl();
                imgurService.deleteImage(imageUrl);
                userService.removeUserImage(user.getId(), imageId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
