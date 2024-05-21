package com.example.synchronyproject.service;

import com.example.synchronyproject.model.Image;
import com.example.synchronyproject.model.User;
import com.example.synchronyproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void addUserImage(Long id, String imageUrl, String imageDeleteHash) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            boolean imageExists = user.getImages().stream()
                    .anyMatch(image -> image.getImageUrl().equals(imageUrl));
            if (!imageExists) {
                Image image = new Image(imageUrl, imageDeleteHash);
                image.setUser(user);
                user.addImage(image);
                userRepository.save(user);
            }
        } else {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
    }

    public void removeUserImage(Long userId, Long imageId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Optional<Image> optionalImage = user.getImages().stream()
                    .filter(image -> image.getId().equals(imageId))
                    .findFirst();
            optionalImage.ifPresent(image -> {
                user.removeImage(image);
                userRepository.save(user);
            });
        } else {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
    }

    public Image findImageById(Long imageId) {
        Optional<User> optionalUser = userRepository.findAll().stream()
                .filter(user -> user.getImages().stream()
                        .anyMatch(image -> image.getId().equals(imageId)))
                .findFirst();
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return user.getImages().stream()
                    .filter(image -> image.getId().equals(imageId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
