package edu.neu.csye6200.model.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
}

