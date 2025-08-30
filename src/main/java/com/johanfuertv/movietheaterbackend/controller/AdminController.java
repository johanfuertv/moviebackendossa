package com.johanfuertv.movietheaterbackend.controller;

import com.johanfuertv.movietheaterbackend.dto.request.MovieRequest;
import com.johanfuertv.movietheaterbackend.dto.response.ApiResponse;
import com.johanfuertv.movietheaterbackend.dto.response.CustomerResponse;
import com.johanfuertv.movietheaterbackend.dto.response.MovieResponse;
import com.johanfuertv.movietheaterbackend.dto.response.PurchaseResponse;
import com.johanfuertv.movietheaterbackend.entity.Purchase;
import com.johanfuertv.movietheaterbackend.service.CustomerService;
import com.johanfuertv.movietheaterbackend.service.MovieService;
import com.johanfuertv.movietheaterbackend.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Administration", description = "Admin management APIs")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    @Autowired
    private MovieService movieService;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private PurchaseService purchaseService;
    
    // Movie Management
    @GetMapping("/movies")
    @Operation(summary = "Get all movies (including inactive)")
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getAllMoviesAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        try {
            Sort.Direction direction = Sort.Direction.fromString(sortDir);
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<MovieResponse> movies = movieService.getAllMoviesAdmin(pageable);
            
            return ResponseEntity.ok(ApiResponse.success("Movies retrieved successfully", movies));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error retrieving movies: " + e.getMessage()));
        }
    }
    
    @PostMapping("/movies")
    @Operation(summary = "Create a new movie")
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(@Valid @RequestBody MovieRequest request) {
        try {
            MovieResponse movie = movieService.createMovie(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Movie created successfully", movie));
                
        } catch (Exception e) {
            logger.error("Error creating movie", e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error creating movie: " + e.getMessage()));
        }
    }
    
    @PutMapping("/movies/{id}")
    @Operation(summary = "Update a movie")
    public ResponseEntity<ApiResponse<MovieResponse>> updateMovie(
            @PathVariable UUID id, @Valid @RequestBody MovieRequest request) {
        
        try {
            MovieResponse movie = movieService.updateMovie(id, request);
            
            return ResponseEntity.ok(ApiResponse.success("Movie updated successfully", movie));
            
        } catch (Exception e) {
            logger.error("Error updating movie: {}", id, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error updating movie: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/movies/{id}/disable")
    @Operation(summary = "Disable a movie")
    public ResponseEntity<ApiResponse<String>> disableMovie(@PathVariable UUID id) {
        try {
            movieService.disableMovie(id);
            
            return ResponseEntity.ok(ApiResponse.success("Movie disabled successfully", "Movie has been disabled"));
            
        } catch (Exception e) {
            logger.error("Error disabling movie: {}", id, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error disabling movie: " + e.getMessage()));
        }
    }
    
    @PostMapping("/movies/{id}/poster")
    @Operation(summary = "Upload movie poster")
    public ResponseEntity<ApiResponse<MovieResponse>> uploadMoviePoster(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        
        try {
            MovieResponse movie = movieService.uploadPoster(id, file);
            
            return ResponseEntity.ok(ApiResponse.success("Poster uploaded successfully", movie));
            
        } catch (Exception e) {
            logger.error("Error uploading poster for movie: {}", id, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error uploading poster: " + e.getMessage()));
        }
    }
    
    // Customer Management
    @GetMapping("/customers")
    @Operation(summary = "Get all customers")
    public ResponseEntity<ApiResponse<Page<CustomerResponse>>> getAllCustomers(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        try {
            Sort.Direction direction = Sort.Direction.fromString(sortDir);
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<CustomerResponse> customers = customerService.getAllCustomers(q, pageable);
            
            return ResponseEntity.ok(ApiResponse.success("Customers retrieved successfully", customers));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error retrieving customers: " + e.getMessage()));
        }
    }
    
    @PatchMapping("/customers/{id}/disable")
    @Operation(summary = "Disable a customer")
    public ResponseEntity<ApiResponse<String>> disableCustomer(@PathVariable UUID id) {
        try {
            customerService.disableCustomer(id);
            
            return ResponseEntity.ok(ApiResponse.success("Customer disabled successfully", "Customer has been disabled"));
            
        } catch (Exception e) {
            logger.error("Error disabling customer: {}", id, e);
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error disabling customer: " + e.getMessage()));
        }
    }
    
    // Purchase Management
    @GetMapping("/purchases")
    @Operation(summary = "Get all purchases with optional filtering")
    public ResponseEntity<ApiResponse<Page<PurchaseResponse>>> getAllPurchases(
            @Parameter(description = "Filter by customer ID")
            @RequestParam(required = false) UUID customerId,
            @Parameter(description = "Filter by movie ID")
            @RequestParam(required = false) UUID movieId,
            @Parameter(description = "Filter by status")
            @RequestParam(required = false) Purchase.PurchaseStatus status,
            @Parameter(description = "Filter by start date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Filter by end date")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        try {
            Sort.Direction direction = Sort.Direction.fromString(sortDir);
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<PurchaseResponse> purchases = purchaseService.getPurchasesWithFilters(
                customerId, movieId, status, startDate, endDate, pageable);
            
            return ResponseEntity.ok(ApiResponse.success("Purchases retrieved successfully", purchases));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error retrieving purchases: " + e.getMessage()));
        }
    }
    
    @GetMapping("/purchases/{id}")
    @Operation(summary = "Get purchase by ID")
    public ResponseEntity<ApiResponse<PurchaseResponse>> getPurchaseById(@PathVariable UUID id) {
        try {
            PurchaseResponse purchase = purchaseService.getPurchaseById(id);
            
            return ResponseEntity.ok(ApiResponse.success("Purchase retrieved successfully", purchase));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Purchase not found: " + e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get purchase statistics")
    public ResponseEntity<ApiResponse<PurchaseService.PurchaseStats>> getPurchaseStats() {
        try {
            PurchaseService.PurchaseStats stats = purchaseService.getPurchaseStats();
            
            return ResponseEntity.ok(ApiResponse.success("Statistics retrieved successfully", stats));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error retrieving statistics: " + e.getMessage()));
        }
    }
}