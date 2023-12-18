package com.example.springsecuritydemo.controller;


import com.example.springsecuritydemo.commons.UserConstants;
import com.example.springsecuritydemo.entity.User;
import com.example.springsecuritydemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/join")
    public String joinTikTok(@RequestBody User user){
        user.setRoles(UserConstants.DEFAULT_USER);
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        User user1 = userRepository.save(user);
        log.info("new user joined -----> {}",user1);
        return "Hi "+user.getEmail()+" welcome to group";
    }

    // if logged in is ADMIN -> ADMIN OR MODERATOR
    //if logged in is MODERATOR -> MODERATOR

    @GetMapping("/access/{userId}/{userRole}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MODERATOR')")
    public String giveAccessToUser(@PathVariable("userId") long userId, @PathVariable("userRole") String userRole, Principal principal){

        Optional<User> user = userRepository.findById(userId);
        User savedUserRole = null;
        if (user.isPresent()){
            User foundUser = user.get();
            log.info("user found -----> {}",foundUser);
            List<String> activeRoles = getRolesByLoggedInUser(principal);
            log.info("active roles -----> {}",activeRoles);
            String newRole = "";

            if (activeRoles.contains(userRole)){
                newRole = foundUser.getRoles()+","+userRole;
                foundUser.setRoles(newRole);
            }
            log.info("new user role added -----> {}",newRole);
            savedUserRole = userRepository.save(foundUser);
            log.info("new user role added -----> {}",savedUserRole);

            return "Hi "+foundUser.getEmail()+" new role assigned to you by "+principal.getName();

        }else {
            log.info("user not found for id -----> {}",userId);
            return savedUserRole.toString();
        }

    }

    @GetMapping
    @Secured("ROLE_ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<User> loadUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String testUser(){
        return "User can access only this";
    }

    private User getLoggedInUser(Principal principal){
        Optional<User> user = userRepository.findByEmail(principal.getName());
        if (user.isPresent()){
            User user1 = user.get();
            log.info("logged in user -----> {}",user1);
            return user1;
        }else {
            log.info("user not found");
            return null;
        }
    }

    private  List<String> getRolesByLoggedInUser(Principal principal){
        String roles = getLoggedInUser(principal).getRoles();
        List<String> assignRoles = Arrays.stream(roles.split(","))
                .collect(Collectors.toList());

        if (assignRoles.contains("ROLE_ADMIN")){
            return Arrays.stream(UserConstants.ADMIN_ACCESS)
                    .collect(Collectors.toList());
        }
        if (assignRoles.contains("ROLE_MODERATOR")){
            return Arrays.stream(UserConstants.MODERATOR_ACCESS)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

}
