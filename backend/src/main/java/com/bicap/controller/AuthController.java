package com.bicap.controller;

import com.bicap.dto.request.auth.LoginRequest;
import com.bicap.dto.request.auth.RegisterRequest;
import com.bicap.dto.response.auth.LoginResponse;
import com.bicap.dto.response.auth.RegisterResponse;
import com.bicap.entity.Account;
import com.bicap.security.CustomUserDetails;
import com.bicap.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ==========================================================
    // LOGIN
    // ==========================================================

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    // ==========================================================
    // REGISTER (Retailer Self Registration)
    // ==========================================================

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        RegisterResponse response = authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==========================================================
    // CURRENT USER
    // ==========================================================

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> me(Authentication authentication) {

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        Account account = user.getAccount();

        return ResponseEntity.ok(
                LoginResponse.builder()
                        .username(account.getUsername())
                        .role(account.getRole().getRoleName())
                        .build()
        );
    }

}