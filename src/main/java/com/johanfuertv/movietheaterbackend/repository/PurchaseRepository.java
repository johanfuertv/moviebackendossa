package com.johanfuertv.movietheaterbackend.repository;

import org.springframework.stereotype.Repository;

import com.johanfuertv.movietheaterbackend.entity.Purchase;
import com.johanfuertv.movietheaterbackend.entity.Purchase.PurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
    
    List<Purchase> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
    
    Page<Purchase> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    List<Purchase> findByStatus(PurchaseStatus status);
    
    @Query("SELECT p FROM Purchase p JOIN p.customer c JOIN p.movie m WHERE " +
           "(:customerId IS NULL OR p.customer.id = :customerId) AND " +
           "(:movieId IS NULL OR p.movie.id = :movieId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:startDate IS NULL OR p.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR p.createdAt <= :endDate) " +
           "ORDER BY p.createdAt DESC")
    Page<Purchase> findPurchasesWithFilters(@Param("customerId") UUID customerId,
                                           @Param("movieId") UUID movieId,
                                           @Param("status") PurchaseStatus status,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           Pageable pageable);
    
    @Query("SELECT SUM(p.totalAmount) FROM Purchase p WHERE p.status = 'PAID'")
    BigDecimal getTotalRevenue();
    
    @Query("SELECT COUNT(p) FROM Purchase p WHERE p.status = 'PAID'")
    Long getTotalTicketsSold();
    
    @Query("SELECT p FROM Purchase p WHERE p.customer.email = :email ORDER BY p.createdAt DESC")
    List<Purchase> findByCustomerEmailOrderByCreatedAtDesc(@Param("email") String email);
}