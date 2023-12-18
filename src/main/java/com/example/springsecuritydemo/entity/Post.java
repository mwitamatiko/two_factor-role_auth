package com.example.springsecuritydemo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "tbl_posts")
@Entity
public class Post {
    @Id
    @GeneratedValue
    private long id;
    private String subject;
    private String description;
    private String email;

    @Enumerated(EnumType.STRING)
    private PostStatus status;
}
