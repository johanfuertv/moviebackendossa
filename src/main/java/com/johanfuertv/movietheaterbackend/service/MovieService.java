package com.johanfuertv.movietheaterbackend.service;

import com.johanfuertv.movietheaterbackend.dto.request.MovieRequest;
import com.johanfuertv.movietheaterbackend.dto.response.MovieResponse;
import com.johanfuertv.movietheaterbackend.entity.Movie;
import com.johanfuertv.movietheaterbackend.exception.ResourceNotFoundException;
import com.johanfuertv.movietheaterbackend.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class MovieService {
    
    private static final Logger logger = LoggerFactory.getLogger(MovieService.class);
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private StorageService storageService;
    
    public Page<MovieResponse> getAllMovies(String query, String genre, Pageable pageable) {
        Page<Movie> movies = movieRepository.findActiveMoviesWithFilters(query, genre, pageable);
        return movies.map(MovieResponse::new);
    }
    
    public MovieResponse getMovieById(UUID id) {
        Movie movie = movieRepository.findByIdAndActiveTrue(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        return new MovieResponse(movie);
    }
    
    public List<String> getGenres() {
        return movieRepository.findDistinctGenres();
    }
    
    
    public Page<MovieResponse> getAllMoviesAdmin(Pageable pageable) {
        Page<Movie> movies = movieRepository.findAll(pageable);
        return movies.map(MovieResponse::new);
    }
    
    public MovieResponse createMovie(MovieRequest request) {
        Movie movie = new Movie();
        updateMovieFromRequest(movie, request);
        
        Movie savedMovie = movieRepository.save(movie);
        logger.info("Movie created: {}", savedMovie.getTitle());
        
        return new MovieResponse(savedMovie);
    }
    
    public MovieResponse updateMovie(UUID id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        
        updateMovieFromRequest(movie, request);
        Movie updatedMovie = movieRepository.save(movie);
        
        logger.info("Movie updated: {}", updatedMovie.getTitle());
        return new MovieResponse(updatedMovie);
    }
    
    public void disableMovie(UUID id) {
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        
        movie.setActive(false);
        movieRepository.save(movie);
        
        logger.info("Movie disabled: {}", movie.getTitle());
    }
    
    public MovieResponse uploadPoster(UUID id, MultipartFile file) {
        Movie movie = movieRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        
        
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("File must be an image");
        }
        
        
        if (movie.getPosterUrl() != null) {
            try {
                storageService.delete(movie.getPosterUrl());
            } catch (Exception e) {
                logger.warn("Could not delete old poster: {}", movie.getPosterUrl(), e);
            }
        }
        
       
        String posterUrl = storageService.store(file, "posters");
        movie.setPosterUrl(posterUrl);
        
        Movie updatedMovie = movieRepository.save(movie);
        logger.info("Poster uploaded for movie: {}", updatedMovie.getTitle());
        
        return new MovieResponse(updatedMovie);
    }
    
    private void updateMovieFromRequest(Movie movie, MovieRequest request) {
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setGenre(request.getGenre());
        movie.setDurationMin(request.getDurationMin());
        movie.setPrice(request.getPrice());
    }
}