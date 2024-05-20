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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Image(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Long getId() {
        return this.id;
    }
}