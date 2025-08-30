package com.johanfuertv.movietheaterbackend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PurchaseResponse {
    private UUID id;
    private CustomerResponse customer;
    private MovieResponse movie;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod;
    private String last4;
    private String paymentNote;
    private LocalDateTime createdAt;
    
    public PurchaseResponse(com.johanfuertv.movietheaterbackend.entity.Purchase purchase) {
        this.id = purchase.getId();
        this.customer = new CustomerResponse(purchase.getCustomer());
        this.movie = new MovieResponse(purchase.getMovie());
        this.quantity = purchase.getQuantity();
        this.totalAmount = purchase.getTotalAmount();
        this.status = purchase.getStatus().name();
        this.paymentMethod = purchase.getPaymentMethod();
        this.last4 = purchase.getLast4();
        this.paymentNote = purchase.getPaymentNote();
        this.createdAt = purchase.getCreatedAt();
    }
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public CustomerResponse getCustomer() { return customer; }
    public void setCustomer(CustomerResponse customer) { this.customer = customer; }
    public MovieResponse getMovie() { return movie; }
    public void setMovie(MovieResponse movie) { this.movie = movie; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getLast4() { return last4; }
    public void setLast4(String last4) { this.last4 = last4; }
    public String getPaymentNote() { return paymentNote; }
    public void setPaymentNote(String paymentNote) { this.paymentNote = paymentNote; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
