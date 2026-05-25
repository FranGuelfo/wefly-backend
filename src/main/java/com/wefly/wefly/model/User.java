package com.wefly.wefly.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String profilePictureUrl;
    private Boolean isVerified;

    @Column(length = 500)
    private String bio;

    @OneToMany(mappedBy = "author")
    private List<Announcement> announcements;
}