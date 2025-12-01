package edu.neu.csye6200.service;

import edu.neu.csye6200.dto.request.CreateBusinessRequest;
import edu.neu.csye6200.dto.request.UpdateBusinessRequest;
import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.repository.BusinessRepository;
import edu.neu.csye6200.service.impl.BusinessServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
@DisplayName("BusinessService Tests")
class BusinessServiceTest {
    
    @Mock
    private BusinessRepository businessRepository;
    
    @InjectMocks
    private BusinessServiceImpl businessService;
    
    private Company testCompany;
    private CreateBusinessRequest createRequest;
    private UpdateBusinessRequest updateRequest;
    
    @BeforeEach
    void setUp() {
        testCompany = new Company("Test Corp", "123 Main St");
        testCompany.setId(1L);
        testCompany.setIndustry("Technology");
        testCompany.setFoundedDate(LocalDate.of(2020, 1, 1));
        
        createRequest = new CreateBusinessRequest(
            "Test Corp",
            "123 Main St",
            "Technology",
            LocalDate.of(2020, 1, 1)
        );
    }
    
    @Test
    @DisplayName("Should create business successfully from CreateBusinessRequest")
    void testCreateBusiness_Success() {
        // Given
        when(businessRepository.save(any(Company.class))).thenReturn(testCompany);
        
        // When
        Company result = businessService.createBusiness(createRequest);
        
        // Then
        assertNotNull(result);
        assertEquals("Test Corp", result.getName());
        assertEquals("123 Main St", result.getAddress());
        assertEquals("Technology", result.getIndustry());
        assertEquals(LocalDate.of(2020, 1, 1), result.getFoundedDate());
        
        // Verify that Company was built correctly and saved
        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
        verify(businessRepository, times(1)).save(companyCaptor.capture());
        Company capturedCompany = companyCaptor.getValue();
        assertEquals("Test Corp", capturedCompany.getName());
        assertEquals("123 Main St", capturedCompany.getAddress());
        assertEquals("Technology", capturedCompany.getIndustry());
        assertEquals(LocalDate.of(2020, 1, 1), capturedCompany.getFoundedDate());
    }
    
    @Test
    @DisplayName("Should create business with null optional fields")
    void testCreateBusiness_WithNullOptionalFields() {
        // Given
        CreateBusinessRequest requestWithNulls = new CreateBusinessRequest(
            "Test Corp",
            null,
            null,
            null
        );
        Company companyWithNulls = new Company("Test Corp", null);
        companyWithNulls.setId(1L);
        when(businessRepository.save(any(Company.class))).thenReturn(companyWithNulls);
        
        // When
        Company result = businessService.createBusiness(requestWithNulls);
        
        // Then
        assertNotNull(result);
        assertEquals("Test Corp", result.getName());
        assertNull(result.getAddress());
        assertNull(result.getIndustry());
        assertNull(result.getFoundedDate());
    }
    
    @Test
    @DisplayName("Should get business by id successfully")
    void testGetBusiness_Success() {
        // Given
        when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        
        // When
        Company result = businessService.getBusiness(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Corp", result.getName());
        assertEquals("123 Main St", result.getAddress());
        verify(businessRepository, times(1)).findById(1L);
    }
    
    @Test
    @DisplayName("Should throw RuntimeException when business not found")
    void testGetBusiness_NotFound() {
        // Given
        when(businessRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> businessService.getBusiness(999L));
        
        assertEquals("Business not found with id: 999", exception.getMessage());
        verify(businessRepository, times(1)).findById(999L);
    }
    
    @Test
    @DisplayName("Should update business with all fields")
    void testUpdateBusiness_AllFields() {
        // Given
        updateRequest = new UpdateBusinessRequest(
            "Updated Corp",
            "456 New St",
            "Finance",
            LocalDate.of(2021, 1, 1)
        );
        when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(businessRepository.save(any(Company.class))).thenReturn(testCompany);
        
        // When
        Company result = businessService.updateBusiness(1L, updateRequest);
        
        // Then
        assertNotNull(result);
        assertEquals("Updated Corp", testCompany.getName());
        assertEquals("456 New St", testCompany.getAddress());
        assertEquals("Finance", testCompany.getIndustry());
        assertEquals(LocalDate.of(2021, 1, 1), testCompany.getFoundedDate());
        verify(businessRepository, times(1)).findById(1L);
        verify(businessRepository, times(1)).save(testCompany);
    }
    
    @Test
    @DisplayName("Should update business with partial fields (only non-null values)")
    void testUpdateBusiness_PartialUpdate() {
        // Given
        updateRequest = new UpdateBusinessRequest(
            "Updated Corp",
            null,
            null,
            null
        );
        when(businessRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(businessRepository.save(any(Company.class))).thenReturn(testCompany);
        
        // When
        Company result = businessService.updateBusiness(1L, updateRequest);
        
        // Then
        assertNotNull(result);
        assertEquals("Updated Corp", testCompany.getName());
        // Original values should remain unchanged
        assertEquals("123 Main St", testCompany.getAddress());
        assertEquals("Technology", testCompany.getIndustry());
        assertEquals(LocalDate.of(2020, 1, 1), testCompany.getFoundedDate());
        verify(businessRepository, times(1)).findById(1L);
        verify(businessRepository, times(1)).save(testCompany);
    }
    
    @Test
    @DisplayName("Should throw RuntimeException when updating non-existent business")
    void testUpdateBusiness_NotFound() {
        // Given
        updateRequest = new UpdateBusinessRequest(
            "Updated Corp",
            "456 New St",
            null,
            null
        );
        when(businessRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> businessService.updateBusiness(999L, updateRequest));
        
        assertEquals("Business not found with id: 999", exception.getMessage());
        verify(businessRepository, times(1)).findById(999L);
        verify(businessRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should delete business successfully")
    void testDeleteBusiness_Success() {
        // Given
        when(businessRepository.existsById(1L)).thenReturn(true);
        doNothing().when(businessRepository).deleteById(1L);
        
        // When
        businessService.deleteBusiness(1L);
        
        // Then
        verify(businessRepository, times(1)).existsById(1L);
        verify(businessRepository, times(1)).deleteById(1L);
    }
    
    @Test
    @DisplayName("Should throw RuntimeException when deleting non-existent business")
    void testDeleteBusiness_NotFound() {
        // Given
        when(businessRepository.existsById(999L)).thenReturn(false);
        
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> businessService.deleteBusiness(999L));
        
        assertEquals("Business not found with id: 999", exception.getMessage());
        verify(businessRepository, times(1)).existsById(999L);
        verify(businessRepository, never()).deleteById(any());
    }
    
    @Test
    @DisplayName("Should get all businesses successfully")
    void testGetAllBusinesses_Success() {
        // Given
        Company company2 = new Company("Another Corp", "789 Second St");
        company2.setId(2L);
        company2.setIndustry("Healthcare");
        List<Company> companies = Arrays.asList(testCompany, company2);
        
        when(businessRepository.findAll()).thenReturn(companies);
        
        // When
        List<Company> result = businessService.getAllBusinesses();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Corp", result.get(0).getName());
        assertEquals("Another Corp", result.get(1).getName());
        verify(businessRepository, times(1)).findAll();
    }
    
    @Test
    @DisplayName("Should return empty list when no businesses exist")
    void testGetAllBusinesses_EmptyList() {
        // Given
        when(businessRepository.findAll()).thenReturn(List.of());
        
        // When
        List<Company> result = businessService.getAllBusinesses();
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(businessRepository, times(1)).findAll();
    }
}
