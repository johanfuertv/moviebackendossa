package com.johanfuertv.movietheaterbackend.service;

import com.johanfuertv.movietheaterbackend.dto.request.PurchaseRequest;
import com.johanfuertv.movietheaterbackend.dto.response.PurchaseResponse;
import com.johanfuertv.movietheaterbackend.entity.Customer;
import com.johanfuertv.movietheaterbackend.entity.Movie;
import com.johanfuertv.movietheaterbackend.entity.Purchase;
import com.johanfuertv.movietheaterbackend.exception.ResourceNotFoundException;
//import com.johanfuertv.movietheaterbackend.repository.CustomerRepository;
import com.johanfuertv.movietheaterbackend.repository.MovieRepository;
import com.johanfuertv.movietheaterbackend.repository.PurchaseRepository;
import com.johanfuertv.movietheaterbackend.security.CustomUserDetailsService.CustomUserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PurchaseService {
    
    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);
    
    @Autowired
    private PurchaseRepository purchaseRepository;
    
    @Autowired
    private MovieRepository movieRepository;
    
    //@Autowired
    //private CustomerRepository customerRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request) {
        // Get current authenticated user
        Customer customer = getCurrentCustomer();
        
        // Validate customer is active
        if (!customer.getActive()) {
            throw new RuntimeException("Customer account is not active");
        }
        
        // Get movie and validate it's available
        Movie movie = movieRepository.findByIdAndActiveTrue(request.getMovieId())
            .orElseThrow(() -> new ResourceNotFoundException("Movie not found or not available: " + request.getMovieId()));
        
        // Create purchase
        Purchase purchase = new Purchase();
        purchase.setCustomer(customer);
        purchase.setMovie(movie);
        purchase.setQuantity(request.getQuantity());
        
        // Calculate total amount
        BigDecimal totalAmount = movie.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));
        purchase.setTotalAmount(totalAmount);
        
        // Set payment information
        purchase.setPaymentMethod(request.getPayment().getMethod());
        purchase.setLast4(request.getPayment().getLast4());
        purchase.setPaymentNote("Payment by " + request.getPayment().getName());
        
        // Simulate payment processing (always successful for now)
        purchase.setStatus(Purchase.PurchaseStatus.PAID);
        
        // Save purchase
        Purchase savedPurchase = purchaseRepository.save(purchase);
        
        logger.info("Purchase created - ID: {}, Customer: {}, Movie: {}, Amount: {}", 
                   savedPurchase.getId(), customer.getEmail(), movie.getTitle(), totalAmount);
        
        // Send confirmation email asynchronously
        try {
            emailService.sendPurchaseConfirmation(savedPurchase);
        } catch (Exception e) {
            logger.error("Error sending purchase confirmation email", e);
            // Don't fail the purchase if email fails
        }
        
        return new PurchaseResponse(savedPurchase);
    }
    
    public List<PurchaseResponse> getMyPurchases() {
        Customer customer = getCurrentCustomer();
        List<Purchase> purchases = purchaseRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId());
        return purchases.stream()
                .map(PurchaseResponse::new)
                .collect(Collectors.toList());
    }
    
    // Admin methods
    public Page<PurchaseResponse> getAllPurchases(Pageable pageable) {
        Page<Purchase> purchases = purchaseRepository.findAllByOrderByCreatedAtDesc(pageable);
        return purchases.map(PurchaseResponse::new);
    }
    
    public Page<PurchaseResponse> getPurchasesWithFilters(UUID customerId, UUID movieId, 
                                                        Purchase.PurchaseStatus status,
                                                        LocalDateTime startDate, LocalDateTime endDate, 
                                                        Pageable pageable) {
        Page<Purchase> purchases = purchaseRepository.findPurchasesWithFilters(
            customerId, movieId, status, startDate, endDate, pageable);
        return purchases.map(PurchaseResponse::new);
    }
    
    public PurchaseResponse getPurchaseById(UUID id) {
        Purchase purchase = purchaseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + id));
        return new PurchaseResponse(purchase);
    }
    
    public PurchaseStats getPurchaseStats() {
        BigDecimal totalRevenue = purchaseRepository.getTotalRevenue();
        Long totalTickets = purchaseRepository.getTotalTicketsSold();
        Long totalPurchases = purchaseRepository.count();
        
        return new PurchaseStats(
            totalRevenue != null ? totalRevenue : BigDecimal.ZERO,
            totalTickets != null ? totalTickets : 0L,
            totalPurchases
        );
    }
    
    private Customer getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal) {
            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
            return userPrincipal.getCustomer();
        }
        
        throw new RuntimeException("No authenticated user found");
    }
    
    // Stats class
    public static class PurchaseStats {
        private BigDecimal totalRevenue;
        private Long totalTickets;
        private Long totalPurchases;
        
        public PurchaseStats(BigDecimal totalRevenue, Long totalTickets, Long totalPurchases) {
            this.totalRevenue = totalRevenue;
            this.totalTickets = totalTickets;
            this.totalPurchases = totalPurchases;
        }
        
        // Getters
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public Long getTotalTickets() { return totalTickets; }
        public Long getTotalPurchases() { return totalPurchases; }
    }
}