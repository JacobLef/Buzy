package edu.neu.csye6200.model.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Business interface defining core operations for business entities
 * 
 * @author Qing Mi
 */
public interface Business {
    
    Long getId();
    
    void setId(Long id);
    
    String getName();
    
    void setName(String name);
    
    String getAddress();
    
    void setAddress(String address);
    
    String getIndustry();
    
    void setIndustry(String industry);
    
    LocalDate getFoundedDate();
    
    void setFoundedDate(LocalDate foundedDate);
    
    LocalDateTime getCreatedAt();
    
    void setCreatedAt(LocalDateTime createdAt);
    
    LocalDateTime getUpdatedAt();
    
    void setUpdatedAt(LocalDateTime updatedAt);
    
    void addEmployee(BusinessPerson employee);
    
    void removeEmployee(BusinessPerson employee);
    
    List<BusinessPerson> getEmployees();
}

