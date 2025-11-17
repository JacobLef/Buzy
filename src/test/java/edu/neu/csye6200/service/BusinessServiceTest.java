package edu.neu.csye6200.service;

import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.repository.BusinessRepository;
import edu.neu.csye6200.service.impl.BusinessServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessServiceTest {
    
    @Mock
    private BusinessRepository businessRepository;
    
    @InjectMocks
    private BusinessServiceImpl businessService;
    
    private Company testCompany;
    
    @BeforeEach
    void setUp() {
        testCompany = new Company("Test Corp", "123 Main St");
        testCompany.setId(1L);
        testCompany.setIndustry("Technology");
        testCompany.setFoundedDate(LocalDate.of(2020, 1, 1));
    }
    
    @Test
    void testCreateBusiness() {
        when(businessRepository.save(any(Company.class))).thenReturn(testCompany);
        
        Company result = businessService.createBusiness(testCompany);
        
        assertNotNull(result);
        assertEquals("Test Corp", result.getName());
        assertEquals("123 Main St", result.getAddress());
        verify(businessRepository, times(1)).save(testCompany);
    }
    
    @Test
    void testGetBusiness() {
        when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        
        Company result = businessService.getBusiness(1L);
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Corp", result.getName());
        verify(businessRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetBusinessNotFound() {
        when(businessRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> businessService.getBusiness(999L));
        verify(businessRepository, times(1)).findById(999L);
    }
    
    @Test
    void testUpdateBusiness() {
        Company updatedData = new Company("Updated Corp", "456 New St");
        updatedData.setIndustry("Finance");
        updatedData.setFoundedDate(LocalDate.of(2021, 1, 1));
        
        when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(businessRepository.save(any(Company.class))).thenReturn(testCompany);
        
        Company result = businessService.updateBusiness(1L, updatedData);
        
        assertNotNull(result);
        verify(businessRepository, times(1)).findById(1L);
        verify(businessRepository, times(1)).save(testCompany);
    }
    
    @Test
    void testUpdateBusinessNotFound() {
        Company updatedData = new Company("Updated Corp", "456 New St");
        when(businessRepository.findById(999L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> businessService.updateBusiness(999L, updatedData));
        verify(businessRepository, times(1)).findById(999L);
        verify(businessRepository, never()).save(any());
    }
    
    @Test
    void testDeleteBusiness() {
        when(businessRepository.existsById(1L)).thenReturn(true);
        
        businessService.deleteBusiness(1L);
        
        verify(businessRepository, times(1)).existsById(1L);
        verify(businessRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteBusinessNotFound() {
        when(businessRepository.existsById(999L)).thenReturn(false);
        
        assertThrows(RuntimeException.class, () -> businessService.deleteBusiness(999L));
        verify(businessRepository, times(1)).existsById(999L);
        verify(businessRepository, never()).deleteById(any());
    }
    
    @Test
    void testGetAllBusinesses() {
        Company company2 = new Company("Another Corp", "789 Second St");
        company2.setId(2L);
        List<Company> companies = Arrays.asList(testCompany, company2);
        
        when(businessRepository.findAll()).thenReturn(companies);
        
        List<Company> result = businessService.getAllBusinesses();
        
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(businessRepository, times(1)).findAll();
    }
}

