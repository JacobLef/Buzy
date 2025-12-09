package edu.neu.csye6200.service.interfaces;

import edu.neu.csye6200.dto.request.CreateBusinessRequest;
import edu.neu.csye6200.dto.request.UpdateBusinessRequest;
import edu.neu.csye6200.model.domain.Company;
import java.util.List;

/**
 * Service interface for business management operations
 */
public interface BusinessService {
    
    Company createBusiness(CreateBusinessRequest request);
    
    Company updateBusiness(Long id, UpdateBusinessRequest request);
    
    void deleteBusiness(Long id);
    
    Company getBusiness(Long id);
    
    List<Company> getAllBusinesses();
}

