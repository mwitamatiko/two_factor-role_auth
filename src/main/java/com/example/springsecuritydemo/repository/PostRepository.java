package com.example.springsecuritydemo.repository;

import com.example.springsecuritydemo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {
}
