package edu.neu.csye6200.model.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Company entity representing a business organization
 */
@Entity
@Table(name = "business")
public class Company implements Business {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String address;
    
    @Column(length = 100)
    private String industry;
    
    @Column(name = "founded_date")
    private LocalDate foundedDate;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BusinessPerson> employees = new ArrayList<>();
    
    public Company() {
    }
    
    public Company(String name, String address) {
        this.name = name;
        this.address = address;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public void addEmployee(BusinessPerson employee) {
        if (employee != null && !employees.contains(employee)) {
            employees.add(employee);
            employee.setBusiness(this);
        }
    }
    
    @Override
    public void removeEmployee(BusinessPerson employee) {
        if (employee != null && employees.remove(employee)) {
            employee.setBusiness(null);
        }
    }
    
    @Override
    public List<BusinessPerson> getEmployees() {
        return new ArrayList<>(employees);
    }
    
    @Override
    public Long getId() {
        return id;
    }
    
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public String getAddress() {
        return address;
    }
    
    @Override
    public void setAddress(String address) {
        this.address = address;
    }
    
    @Override
    public String getIndustry() {
        return industry;
    }
    
    @Override
    public void setIndustry(String industry) {
        this.industry = industry;
    }
    
    @Override
    public LocalDate getFoundedDate() {
        return foundedDate;
    }
    
    @Override
    public void setFoundedDate(LocalDate foundedDate) {
        this.foundedDate = foundedDate;
    }
    
    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public static class Builder {
        private String name;
        private String address;
        private String industry;
        private LocalDate foundedDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        
        public Builder industry(String industry) {
            this.industry = industry;
            return this;
        }
        
        public Builder foundedDate(LocalDate foundedDate) {
            this.foundedDate = foundedDate;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }
        
        /**
         * Creates a Builder instance from a CSV row map.
         * 
         * @param csvRow Map containing CSV column names as keys and values as values
         * @return Builder instance configured with CSV data
         * @throws IllegalArgumentException if required fields are missing or invalid
         */
        public static Builder fromCSV(Map<String, String> csvRow) {
            if (csvRow == null) {
                throw new IllegalArgumentException("CSV row cannot be null");
            }
            
            Builder builder = new Builder();
            
            String name = csvRow.get("name");
            if (name != null && !name.trim().isEmpty()) {
                builder.name(name.trim());
            }
            
            String address = csvRow.get("address");
            if (address != null && !address.trim().isEmpty()) {
                builder.address(address.trim());
            }
            
            String industry = csvRow.get("industry");
            if (industry != null && !industry.trim().isEmpty()) {
                builder.industry(industry.trim());
            }
            
            String foundedDateStr = csvRow.get("founded_date");
            if (foundedDateStr != null && !foundedDateStr.trim().isEmpty()) {
                try {
                    LocalDate foundedDate = LocalDate.parse(foundedDateStr.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                    builder.foundedDate(foundedDate);
                } catch (DateTimeParseException e) {
                    System.err.println("Warning: Invalid date format for founded_date: " + foundedDateStr + ". Skipping date.");
                }
            }
            
            return builder;
        }
        
        public Company build() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Company name is required");
            }
            
            Company company = new Company();
            company.setName(name);
            company.setAddress(address);
            company.setIndustry(industry);
            company.setFoundedDate(foundedDate);
            
            LocalDateTime now = LocalDateTime.now();
            company.setCreatedAt(createdAt != null ? createdAt : now);
            company.setUpdatedAt(updatedAt != null ? updatedAt : now);
            
            return company;
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}

