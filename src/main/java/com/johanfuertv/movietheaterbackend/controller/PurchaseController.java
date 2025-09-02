package com.johanfuertv.movietheaterbackend.controller;

import com.johanfuertv.movietheaterbackend.dto.request.PurchaseRequest;
import com.johanfuertv.movietheaterbackend.dto.response.ApiResponse;
import com.johanfuertv.movietheaterbackend.dto.response.PurchaseResponse;
import com.johanfuertv.movietheaterbackend.service.EmailService;
import com.johanfuertv.movietheaterbackend.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@Tag(name = "Purchases", description = "Purchase management APIs")
@SecurityRequirement(name = "Bearer Authentication")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PurchaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);
    
    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private EmailService emailService;
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a new purchase (requires USER role)")
    public ResponseEntity<ApiResponse<PurchaseResponse>> createPurchase(@Valid @RequestBody PurchaseRequest request) {
        try {
            PurchaseResponse purchase = purchaseService.createPurchase(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Purchase created successfully", purchase));
                
        } catch (Exception e) {
            logger.error("Error creating purchase", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Purchase failed: " + e.getMessage()));
        }
    }
    
    @GetMapping("/my-purchases")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get current user's purchases")
    public ResponseEntity<ApiResponse<List<PurchaseResponse>>> getMyPurchases() {
        try {
            List<PurchaseResponse> purchases = purchaseService.getMyPurchases();
            
            return ResponseEntity.ok(ApiResponse.success("Purchases retrieved successfully", purchases));
            
        } catch (Exception e) {
            logger.error("Error retrieving user purchases", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error retrieving purchases: " + e.getMessage()));
        }
    }
}