package app.business;

import app.business.dto.CreateBusinessRequest;
import app.business.dto.UpdateBusinessRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service implementation for business management operations
 */
@Service
@Transactional
public class BusinessServiceImpl implements BusinessService {

	private final BusinessRepository businessRepository;

	@Autowired
	public BusinessServiceImpl(BusinessRepository businessRepository) {
		this.businessRepository = businessRepository;
	}

	@Override
	public Company createBusiness(CreateBusinessRequest request) {
		Company company = Company.builder().name(request.name()).address(request.address()).industry(request.industry())
				.foundedDate(request.foundedDate()).build();
		return businessRepository.save(company);
	}

	@Override
	public Company updateBusiness(Long id, UpdateBusinessRequest request) {
		Optional<Company> existingCompany = businessRepository.findById(id);
		if (existingCompany.isPresent()) {
			Company companyToUpdate = existingCompany.get();
			if (request.name() != null) {
				companyToUpdate.setName(request.name());
			}
			if (request.address() != null) {
				companyToUpdate.setAddress(request.address());
			}
			if (request.industry() != null) {
				companyToUpdate.setIndustry(request.industry());
			}
			if (request.foundedDate() != null) {
				companyToUpdate.setFoundedDate(request.foundedDate());
			}
			return businessRepository.save(companyToUpdate);
		}
		throw new RuntimeException("Business not found with id: " + id);
	}

	@Override
	public void deleteBusiness(Long id) {
		if (businessRepository.existsById(id)) {
			businessRepository.deleteById(id);
		} else {
			throw new RuntimeException("Business not found with id: " + id);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Company getBusiness(Long id) {
		return businessRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Business not found with id: " + id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Company> getAllBusinesses() {
		return businessRepository.findAll();
	}
}
