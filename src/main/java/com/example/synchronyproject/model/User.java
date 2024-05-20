package com.example.synchronyproject.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "app_user")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("user")
    private List<Image> images = new ArrayList<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void addImage(Image image) {
        images.add(image);
        image.setUser(this);
    }

    public void removeImage(Image image) {
        images.remove(image);
        image.setUser(null);
    }

    public CharSequence getPassword() {
        return this.password;
    }


    public void setPassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public List<Image> getImages() {
        return this.images;
    }

    public Long getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }
}