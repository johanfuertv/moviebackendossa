package com.johanfuertv.movietheaterbackend.controller;

import com.johanfuertv.movietheaterbackend.dto.request.LoginRequest;
import com.johanfuertv.movietheaterbackend.dto.request.RegisterRequest;
import com.johanfuertv.movietheaterbackend.dto.response.ApiResponse;
import com.johanfuertv.movietheaterbackend.dto.response.CustomerResponse;
import com.johanfuertv.movietheaterbackend.dto.response.LoginResponse;
import com.johanfuertv.movietheaterbackend.service.AuthService;
import com.johanfuertv.movietheaterbackend.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private EmailService emailService;
    
    @PostMapping("/register")
    @Operation(summary = "Register a new customer")
    public ResponseEntity<ApiResponse<CustomerResponse>> registerCustomer(@Valid @RequestBody RegisterRequest request) {
        try {
            CustomerResponse customer = authService.register(request);
            
            
            try {
                emailService.sendWelcomeEmail(customer.getEmail(), customer.getFirstName());
            } catch (Exception e) {
                logger.warn("Failed to send welcome email to: {}", customer.getEmail(), e);
            }
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer registered successfully", customer));
                
        } catch (Exception e) {
            logger.error("Registration failed for email: {}", request.getEmail(), e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    @Operation(summary = "Authenticate customer and return JWT token")
    public ResponseEntity<ApiResponse<LoginResponse>> loginCustomer(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse loginResponse = authService.login(request);
            
            return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
            
        } catch (Exception e) {
            logger.error("Login failed for email: {}", request.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Invalid credentials"));
        }
    }
}