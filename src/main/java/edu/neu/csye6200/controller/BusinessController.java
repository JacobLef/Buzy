package edu.neu.csye6200.controller;

import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.service.interfaces.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for business management operations
 */
@RestController
@RequestMapping("/api/businesses")
public class BusinessController {
    
    private final BusinessService businessService;
    
    @Autowired
    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }
    
    // Spring boot will automatically map the request body to the company object
    // example: POST /api/businesses HTTP/1.1
    // Content-Type: application/json
    // {
    //     "name": "Acme Inc.",
    //     "address": "123 Main St, Anytown, USA",
    //     "industry": "Technology",
    //     "foundedDate": "2020-01-01"
    // }
    @PostMapping
    public ResponseEntity<Map<String, Object>> createBusiness(@RequestBody Company company) {
        Map<String, Object> response = new HashMap<>();
        try {
            Company createdBusiness = businessService.createBusiness(company);
            response.put("status", "success");
            response.put("message", "Business created successfully");
            response.put("data", createdBusiness);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBusiness(
            @PathVariable Long id,
            @RequestBody Company company) {
        Map<String, Object> response = new HashMap<>();
        try {
            Company updatedBusiness = businessService.updateBusiness(id, company);
            response.put("status", "success");
            response.put("message", "Business updated successfully");
            response.put("data", updatedBusiness);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBusiness(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            businessService.deleteBusiness(id);
            response.put("status", "success");
            response.put("message", "Business deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBusiness(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Company business = businessService.getBusiness(id);
            response.put("status", "success");
            response.put("data", business);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBusinesses() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Company> businesses = businessService.getAllBusinesses();
            response.put("status", "success");
            response.put("data", businesses);
            response.put("count", businesses.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

