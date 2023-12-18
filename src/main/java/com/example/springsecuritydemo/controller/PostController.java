package com.example.springsecuritydemo.controller;


import com.example.springsecuritydemo.entity.Post;
import com.example.springsecuritydemo.entity.PostStatus;
import com.example.springsecuritydemo.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
@Slf4j
public class PostController {

    @Autowired
    private  PostRepository repository;

    @PostMapping("/create")
    public String createPost(@RequestBody Post post, Principal principal){
        post.setStatus(PostStatus.PENDING);
        post.setEmail(principal.getName());
        Post  savedPost = repository.save(post);
        log.info("saved post --> {}",savedPost);
        return  principal.getName()+" your post published successfully, requires ADMIN/MODERATOR action!";
    }

    @GetMapping("/approve/{postId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String approvePost(@PathVariable long postId){
        Optional<Post> post = repository.findById(postId);

        if (post.isPresent()){
            Post post1 = post.get();
            log.info("post found {}",post1);
            post1.setStatus(PostStatus.APPROVED);
            log.info("post new status {}",post1);
            repository.save(post1);
        }

        return "Post Approved";
    }

    @GetMapping("/approveall")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String approveAll(){
        repository.findAll().stream()
                .filter(post -> post.getStatus().equals(PostStatus.PENDING))
                .forEach(post -> {
                    post.setStatus(PostStatus.APPROVED);
                    repository.save(post);
                });

        return "All Posts Approved";
    }

    @GetMapping("/remove/{postId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String removePost(@PathVariable long postId){
        Optional<Post> post = repository.findById(postId);

        if (post.isPresent()){
            Post post1 = post.get();
            log.info("post found {}",post1);
            post1.setStatus(PostStatus.REJECTED);
            log.info("post new status {}",post1);
            repository.save(post1);
        }

        return "Post Rejected";
    }

    @GetMapping("/removeall")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String removeAll(){
        repository.findAll().stream()
                .filter(post -> post.getStatus().equals(PostStatus.PENDING))
                .forEach(post -> {
                    post.setStatus(PostStatus.REJECTED);
                    repository.save(post);
                });

        return "All Posts Rejected";
    }

    @GetMapping("/viewall")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Post> viewALl(){
        return repository.findAll();
//                .stream()
//                .filter(post -> post.getStatus().equals(PostStatus.APPROVED))
//                .collect(Collectors.toList());
    }

}
