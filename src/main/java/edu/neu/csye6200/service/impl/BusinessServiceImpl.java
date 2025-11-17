package edu.neu.csye6200.service.impl;

import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.repository.BusinessRepository;
import edu.neu.csye6200.service.interfaces.BusinessService;
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
    public Company createBusiness(Company company) {
        return businessRepository.save(company);
    }
    
    @Override
    public Company updateBusiness(Long id, Company company) {
        Optional<Company> existingCompany = businessRepository.findById(id);
        if (existingCompany.isPresent()) {
            Company companyToUpdate = existingCompany.get();
            companyToUpdate.setName(company.getName());
            companyToUpdate.setAddress(company.getAddress());
            companyToUpdate.setIndustry(company.getIndustry());
            companyToUpdate.setFoundedDate(company.getFoundedDate());
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

