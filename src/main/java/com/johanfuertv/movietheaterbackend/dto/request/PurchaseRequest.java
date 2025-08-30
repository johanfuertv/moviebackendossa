package com.johanfuertv.movietheaterbackend.dto.request;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class PurchaseRequest {
    @NotNull(message = "Movie ID is required")
    private UUID movieId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @Valid
    @NotNull(message = "Payment information is required")
    private PaymentInfo payment;
    
    // Getters and setters
    public UUID getMovieId() { return movieId; }
    public void setMovieId(UUID movieId) { this.movieId = movieId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public PaymentInfo getPayment() { return payment; }
    public void setPayment(PaymentInfo payment) { this.payment = payment; }
    
    public static class PaymentInfo {
        @NotNull(message = "Payment method is required")
        private String method;
        
        private String name;
        private String last4;
        
        // Getters and setters
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLast4() { return last4; }
        public void setLast4(String last4) { this.last4 = last4; }
    }
}
