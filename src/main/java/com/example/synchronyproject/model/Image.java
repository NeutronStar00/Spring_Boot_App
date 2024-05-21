package com.example.synchronyproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private String imageDeleteHash; // Add this field

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Image(String imageUrl, String imageDeleteHash) {
        this.imageUrl = imageUrl;
        this.imageDeleteHash = imageDeleteHash;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getImageDeleteHash() {
        return this.imageDeleteHash;
    }

    public Long getId() {
        return this.id;
    }

    public User getUser() {
        return this.user;
    }
}
