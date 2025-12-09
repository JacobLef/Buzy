package app.business;

import app.business.dto.CreateBusinessRequest;
import app.business.dto.UpdateBusinessRequest;
import app.business.Company;
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

