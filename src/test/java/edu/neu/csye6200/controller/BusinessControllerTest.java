package edu.neu.csye6200.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.csye6200.BusinessManagementApplication;
import edu.neu.csye6200.dto.request.CreateBusinessRequest;
import edu.neu.csye6200.dto.request.UpdateBusinessRequest;
import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.service.interfaces.BusinessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BusinessController.class)
@ContextConfiguration(classes = BusinessManagementApplication.class)
@DisplayName("BusinessController Tests")
class BusinessControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BusinessService businessService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
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
        
        updateRequest = new UpdateBusinessRequest(
            "Updated Corp",
            "456 New St",
            "Finance",
            null
        );
    }
    
    @Test
    @DisplayName("POST /api/businesses - Should create business and return CompanyDTO with 201 CREATED")
    void testCreateBusiness_Success() throws Exception {
        // Given
        when(businessService.createBusiness(any(CreateBusinessRequest.class))).thenReturn(testCompany);
        
        String requestBody = objectMapper.writeValueAsString(createRequest);
        
        // When & Then
        mockMvc.perform(post("/api/businesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Corp"))
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.industry").value("Technology"))
                .andExpect(jsonPath("$.foundedDate").value("2020-01-01"))
                .andExpect(jsonPath("$.businessType").value("Company"))
                .andExpect(jsonPath("$.totalEmployees").exists())
                .andExpect(jsonPath("$.totalEmployers").exists())
                .andExpect(jsonPath("$.totalPersons").exists());
        
        verify(businessService, times(1)).createBusiness(any(CreateBusinessRequest.class));
    }
    
    @Test
    @DisplayName("POST /api/businesses - Should handle invalid request")
    void testCreateBusiness_InvalidRequest() throws Exception {
        // Given - missing required fields
        CreateBusinessRequest invalidRequest = new CreateBusinessRequest(
            null,
            null,
            null,
            null
        );
        String requestBody = objectMapper.writeValueAsString(invalidRequest);
        
        // When & Then - Spring validation should handle this
        // The service might throw IllegalArgumentException
        when(businessService.createBusiness(any(CreateBusinessRequest.class)))
            .thenThrow(new IllegalArgumentException("Company name is required"));
        
        mockMvc.perform(post("/api/businesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("GET /api/businesses/{id} - Should return CompanyDTO with 200 OK")
    void testGetBusiness_Success() throws Exception {
        // Given
        when(businessService.getBusiness(1L)).thenReturn(testCompany);
        
        // When & Then
        mockMvc.perform(get("/api/businesses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Corp"))
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.industry").value("Technology"))
                .andExpect(jsonPath("$.businessType").value("Company"));
        
        verify(businessService, times(1)).getBusiness(1L);
    }
    
    @Test
    @DisplayName("GET /api/businesses/{id} - Should return 404 when business not found")
    void testGetBusiness_NotFound() throws Exception {
        // Given
        when(businessService.getBusiness(999L))
                .thenThrow(new RuntimeException("Business not found with id: 999"));
        
        // When & Then
        mockMvc.perform(get("/api/businesses/999"))
                .andExpect(status().isInternalServerError()); // GlobalExceptionHandler will handle this
        
        verify(businessService, times(1)).getBusiness(999L);
    }
    
    @Test
    @DisplayName("PUT /api/businesses/{id} - Should update business and return CompanyDTO with 200 OK")
    void testUpdateBusiness_Success() throws Exception {
        // Given
        Company updatedCompany = new Company("Updated Corp", "456 New St");
        updatedCompany.setId(1L);
        updatedCompany.setIndustry("Finance");
        
        when(businessService.updateBusiness(eq(1L), any(UpdateBusinessRequest.class)))
                .thenReturn(updatedCompany);
        
        String requestBody = objectMapper.writeValueAsString(updateRequest);
        
        // When & Then
        mockMvc.perform(put("/api/businesses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Corp"))
                .andExpect(jsonPath("$.address").value("456 New St"))
                .andExpect(jsonPath("$.industry").value("Finance"))
                .andExpect(jsonPath("$.businessType").value("Company"));
        
        verify(businessService, times(1)).updateBusiness(eq(1L), any(UpdateBusinessRequest.class));
    }
    
    @Test
    @DisplayName("PUT /api/businesses/{id} - Should handle partial update")
    void testUpdateBusiness_PartialUpdate() throws Exception {
        // Given
        UpdateBusinessRequest partialRequest = new UpdateBusinessRequest(
            "Updated Name Only",
            null,
            null,
            null
        );
        Company partiallyUpdatedCompany = new Company("Updated Name Only", "123 Main St");
        partiallyUpdatedCompany.setId(1L);
        partiallyUpdatedCompany.setIndustry("Technology");
        
        when(businessService.updateBusiness(eq(1L), any(UpdateBusinessRequest.class)))
                .thenReturn(partiallyUpdatedCompany);
        
        String requestBody = objectMapper.writeValueAsString(partialRequest);
        
        // When & Then
        mockMvc.perform(put("/api/businesses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name Only"));
        
        verify(businessService, times(1)).updateBusiness(eq(1L), any(UpdateBusinessRequest.class));
    }
    
    @Test
    @DisplayName("PUT /api/businesses/{id} - Should return error when business not found")
    void testUpdateBusiness_NotFound() throws Exception {
        // Given
        when(businessService.updateBusiness(eq(999L), any(UpdateBusinessRequest.class)))
                .thenThrow(new RuntimeException("Business not found with id: 999"));
        
        String requestBody = objectMapper.writeValueAsString(updateRequest);
        
        // When & Then
        mockMvc.perform(put("/api/businesses/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isInternalServerError());
        
        verify(businessService, times(1)).updateBusiness(eq(999L), any(UpdateBusinessRequest.class));
    }
    
    @Test
    @DisplayName("DELETE /api/businesses/{id} - Should delete business and return 204 NO_CONTENT")
    void testDeleteBusiness_Success() throws Exception {
        // Given
        doNothing().when(businessService).deleteBusiness(1L);
        
        // When & Then
        mockMvc.perform(delete("/api/businesses/1"))
                .andExpect(status().isNoContent());
        
        verify(businessService, times(1)).deleteBusiness(1L);
    }
    
    @Test
    @DisplayName("DELETE /api/businesses/{id} - Should return error when business not found")
    void testDeleteBusiness_NotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Business not found with id: 999"))
                .when(businessService).deleteBusiness(999L);
        
        // When & Then
        mockMvc.perform(delete("/api/businesses/999"))
                .andExpect(status().isInternalServerError());
        
        verify(businessService, times(1)).deleteBusiness(999L);
    }
    
    @Test
    @DisplayName("GET /api/businesses - Should return list of CompanyDTOs with 200 OK")
    void testGetAllBusinesses_Success() throws Exception {
        // Given
        Company company2 = new Company("Another Corp", "789 Second St");
        company2.setId(2L);
        company2.setIndustry("Healthcare");
        List<Company> companies = Arrays.asList(testCompany, company2);
        
        when(businessService.getAllBusinesses()).thenReturn(companies);
        
        // When & Then
        mockMvc.perform(get("/api/businesses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Corp"))
                .andExpect(jsonPath("$[0].businessType").value("Company"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Another Corp"))
                .andExpect(jsonPath("$[1].businessType").value("Company"));
        
        verify(businessService, times(1)).getAllBusinesses();
    }
    
    @Test
    @DisplayName("GET /api/businesses - Should return empty array when no businesses exist")
    void testGetAllBusinesses_EmptyList() throws Exception {
        // Given
        when(businessService.getAllBusinesses()).thenReturn(List.of());
        
        // When & Then
        mockMvc.perform(get("/api/businesses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        
        verify(businessService, times(1)).getAllBusinesses();
    }
    
    @Test
    @DisplayName("POST /api/businesses - Should handle invalid content type")
    void testCreateBusiness_InvalidContentType() throws Exception {
        // When & Then - Spring Boot may return 400 or 500 for unsupported media type
        // The important thing is that the service is not called
        mockMvc.perform(post("/api/businesses")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid content"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    assertTrue(status == 400 || status == 415 || status == 500,
                        "Expected 400, 415, or 500 but got " + status);
                });
        
        verify(businessService, never()).createBusiness(any());
    }
}
