package com.example.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.user.dto.CreateProfileRequest;
import com.example.user.dto.UpdateProfileRequest;
import com.example.user.dto.UserProfileResponse;
import com.example.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService _userService;

    // Lo llama auth-service internamente via Feign
    @PostMapping("/profile")
    public ResponseEntity<Void> createProfile(
            @Valid @RequestBody CreateProfileRequest request) {
    	_userService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Lo llama el usuario autenticado
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(
            @RequestHeader("X-Username") String email) {
        return ResponseEntity.ok(_userService.getProfile(email));
    }

    // Lo llama el usuario autenticado
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestHeader("X-Username") String email,
            @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(_userService.updateProfile(email, request));
    }
}
