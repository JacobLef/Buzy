package edu.neu.csye6200.model.domain;

import java.util.List;

/**
 * Business interface defining core operations for business entities
 */
public interface Business {
    
    /**
     * Get the name of the business
     * @return business name
     */
    String getName();
    
    /**
     * Add an employee to the business
     * @param employee employee to add
     */
    void addEmployee(BusinessPerson employee);
    
    /**
     * Remove an employee from the business
     * @param employee employee to remove
     */
    void removeEmployee(BusinessPerson employee);
    
    /**
     * Get all employees of the business
     * @return list of employees
     */
    List<BusinessPerson> getEmployees();
}

