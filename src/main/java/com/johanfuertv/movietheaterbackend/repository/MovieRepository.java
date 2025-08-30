package com.johanfuertv.movietheaterbackend.repository;


import com.johanfuertv.movietheaterbackend. entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    
    Page<Movie> findByActiveTrue(Pageable pageable);
    
    Optional<Movie> findByIdAndActiveTrue(UUID id);
    
  @Query("SELECT m FROM Movie m WHERE m.active = true AND " +
       "(:query IS NULL OR m.title ILIKE :query OR m.description ILIKE :query) AND " +
       "(:genre IS NULL OR m.genre ILIKE :genre)")
Page<Movie> findActiveMoviesWithFilters(@Param("query") String query, 
                                        @Param("genre") String genre, 
                                        Pageable pageable);

    @Query("SELECT DISTINCT m.genre FROM Movie m WHERE m.active = true ORDER BY m.genre")
    List<String> findDistinctGenres();
    
    List<Movie> findByGenreIgnoreCaseAndActiveTrue(String genre);
}
