package com.johanfuertv.movietheaterbackend.security;

import com.johanfuertv.movietheaterbackend.entity.Customer;
import com.johanfuertv.movietheaterbackend.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        
        if (!customer.getActive()) {
            throw new UsernameNotFoundException("User account is disabled: " + email);
        }
        
        return new CustomUserPrincipal(customer);
    }
    
    public static class CustomUserPrincipal implements UserDetails {
        private Customer customer;
        
        public CustomUserPrincipal(Customer customer) {
            this.customer = customer;
        }
        
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Arrays.stream(customer.getRoles().split(","))
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                    .collect(Collectors.toList());
        }
        
        @Override
        public String getPassword() {
            return customer.getPasswordHash();
        }
        
        @Override
        public String getUsername() {
            return customer.getEmail();
        }
        
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }
        
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }
        
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }
        
        @Override
        public boolean isEnabled() {
            return customer.getActive();
        }
        
        public Customer getCustomer() {
            return customer;
        }
    }
}