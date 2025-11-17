package edu.neu.csye6200.model.domain;

import java.util.List;

/**
 * Business interface defining core operations for business entities
 */
public interface Business {
    
    String getName();
    
    void addEmployee(BusinessPerson employee);
    
    void removeEmployee(BusinessPerson employee);
    
    List<BusinessPerson> getEmployees();
}

