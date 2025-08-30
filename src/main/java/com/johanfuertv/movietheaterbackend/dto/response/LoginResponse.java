package com.johanfuertv.movietheaterbackend.dto.response;

import java.util.List;

public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long expiresIn;
    private CustomerResponse user;
    private List<String> roles;
    
    public LoginResponse(String token, Long expiresIn, CustomerResponse user, List<String> roles) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
        this.roles = roles;
    }
    
    // Getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
    public CustomerResponse getUser() { return user; }
    public void setUser(CustomerResponse user) { this.user = user; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}