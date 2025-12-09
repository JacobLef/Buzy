package app.service.interfaces;

import app.dto.request.CreateBusinessRequest;
import app.dto.request.UpdateBusinessRequest;
import app.model.domain.Company;
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

