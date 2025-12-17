package app.business;

import java.util.List;

import app.business.dto.CreateBusinessRequest;
import app.business.dto.UpdateBusinessRequest;

/** Service interface for business management operations */
public interface BusinessService {

  Company createBusiness(CreateBusinessRequest request);

  Company updateBusiness(Long id, UpdateBusinessRequest request);

  void deleteBusiness(Long id);

  Company getBusiness(Long id);

  List<Company> getAllBusinesses();
}
