package com.johanfuertv.movietheaterbackend.service;

import com.johanfuertv.movietheaterbackend.dto.response.CustomerResponse;
import com.johanfuertv.movietheaterbackend.entity.Customer;
import com.johanfuertv.movietheaterbackend.exception.ResourceNotFoundException;
import com.johanfuertv.movietheaterbackend.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    
    @Autowired
    private CustomerRepository customerRepository;
    
    public Page<CustomerResponse> getAllCustomers(String query, Pageable pageable) {
        Page<Customer> customers;
        
        if (query != null && !query.trim().isEmpty()) {
            customers = customerRepository.findActiveCustomersWithQuery(query.trim(), pageable);
        } else {
            customers = customerRepository.findByActiveTrue(pageable);
        }
        
        return customers.map(CustomerResponse::new);
    }
    
    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        return new CustomerResponse(customer);
    }
    
    public void disableCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        customer.setActive(false);
        customerRepository.save(customer);
        
        logger.info("Customer disabled: {} ({})", customer.getEmail(), customer.getFullName());
    }
    
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }
}