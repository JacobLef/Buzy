package edu.neu.csye6200.service.interfaces;

import edu.neu.csye6200.model.domain.Company;
import java.util.List;

/**
 * Service interface for business management operations
 */
public interface BusinessService {
    
    Company createBusiness(Company company);
    
    Company updateBusiness(Long id, Company company);
    
    void deleteBusiness(Long id);
    
    Company getBusiness(Long id);
    
    List<Company> getAllBusinesses();
}

