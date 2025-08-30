package com.johanfuertv.movietheaterbackend.controller;

import com.johanfuertv.movietheaterbackend.dto.response.ApiResponse;
import com.johanfuertv.movietheaterbackend.dto.response.MovieResponse;
import com.johanfuertv.movietheaterbackend.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
@Tag(name = "Movies", description = "Movie catalog APIs (Public)")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MovieController {
    
    @Autowired
    private MovieService movieService;
    
    @GetMapping
    @Operation(summary = "Get all active movies with optional search and filtering")
    public ResponseEntity<ApiResponse<Page<MovieResponse>>> getAllMovies(
            @Parameter(description = "Search query for title or description")
            @RequestParam(required = false) String q,
            @Parameter(description = "Filter by genre")
            @RequestParam(required = false) String genre,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)")
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        try {
            Sort.Direction direction = Sort.Direction.fromString(sortDir);
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<MovieResponse> movies = movieService.getAllMovies(q, genre, pageable);
            
            return ResponseEntity.ok(ApiResponse.success("Movies retrieved successfully", movies));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error retrieving movies: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get movie by ID")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieById(
            @Parameter(description = "Movie ID")
            @PathVariable UUID id) {
        
        try {
            MovieResponse movie = movieService.getMovieById(id);
            return ResponseEntity.ok(ApiResponse.success("Movie retrieved successfully", movie));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Movie not found: " + e.getMessage()));
        }
    }
    
    @GetMapping("/genres")
    @Operation(summary = "Get all available genres")
    public ResponseEntity<ApiResponse<List<String>>> getGenres() {
        try {
            List<String> genres = movieService.getGenres();
            return ResponseEntity.ok(ApiResponse.success("Genres retrieved successfully", genres));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Error retrieving genres: " + e.getMessage()));
        }
    }
}