package com.johanfuertv.movietheaterbackend.service;

import com.johanfuertv.movietheaterbackend.dto.request.LoginRequest;
import com.johanfuertv.movietheaterbackend.dto.request.RegisterRequest;
import com.johanfuertv.movietheaterbackend.dto.response.CustomerResponse;
import com.johanfuertv.movietheaterbackend.dto.response.LoginResponse;
import com.johanfuertv.movietheaterbackend.entity.Customer;
import com.johanfuertv.movietheaterbackend.repository.CustomerRepository;
import com.johanfuertv.movietheaterbackend.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    public CustomerResponse register(RegisterRequest request) {
        // Check if email already exists
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        // Create new customer
        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        customer.setRoles("USER");
        customer.setActive(true);
        
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("New customer registered: {}", savedCustomer.getEmail());
        
        return new CustomerResponse(savedCustomer);
    }
    
    public LoginResponse login(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Generate JWT token
            String token = tokenProvider.generateToken(userDetails);
            
            // Get customer details
            Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Prepare response
            CustomerResponse customerResponse = new CustomerResponse(customer);
            List<String> roles = Arrays.asList(customer.getRoles().split(","));
            
            logger.info("User logged in successfully: {}", customer.getEmail());
            
            return new LoginResponse(token, tokenProvider.getJwtExpiration(), customerResponse, roles);
            
        } catch (Exception e) {
            logger.error("Login failed for user: {}", request.getEmail(), e);
            throw new RuntimeException("Invalid credentials");
        }
    }
}