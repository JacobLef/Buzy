package edu.neu.csye6200.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.csye6200.model.domain.Company;
import edu.neu.csye6200.service.interfaces.BusinessService;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BusinessController.class)
@ContextConfiguration(classes = {BusinessController.class})
class BusinessControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BusinessService businessService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Company testCompany;
    
    @BeforeEach
    void setUp() {
        testCompany = new Company("Test Corp", "123 Main St");
        testCompany.setId(1L);
        testCompany.setIndustry("Technology");
        testCompany.setFoundedDate(LocalDate.of(2020, 1, 1));
    }
    
    @Test
    void testCreateBusiness() throws Exception {
        when(businessService.createBusiness(any(Company.class))).thenReturn(testCompany);
        
        String requestBody = objectMapper.writeValueAsString(testCompany);
        
        mockMvc.perform(post("/api/businesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Business created successfully"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Test Corp"))
                .andExpect(jsonPath("$.data.address").value("123 Main St"));
        
        verify(businessService, times(1)).createBusiness(any(Company.class));
    }
    
    @Test
    void testGetBusiness() throws Exception {
        when(businessService.getBusiness(1L)).thenReturn(testCompany);
        
        mockMvc.perform(get("/api/businesses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Test Corp"));
        
        verify(businessService, times(1)).getBusiness(1L);
    }
    
    @Test
    void testGetBusinessNotFound() throws Exception {
        when(businessService.getBusiness(999L))
                .thenThrow(new RuntimeException("Business not found with id: 999"));
        
        mockMvc.perform(get("/api/businesses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Business not found with id: 999"));
        
        verify(businessService, times(1)).getBusiness(999L);
    }
    
    @Test
    void testUpdateBusiness() throws Exception {
        Company updatedCompany = new Company("Updated Corp", "456 New St");
        updatedCompany.setId(1L);
        updatedCompany.setIndustry("Finance");
        
        when(businessService.updateBusiness(eq(1L), any(Company.class))).thenReturn(updatedCompany);
        
        String requestBody = objectMapper.writeValueAsString(updatedCompany);
        
        mockMvc.perform(put("/api/businesses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Business updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Updated Corp"));
        
        verify(businessService, times(1)).updateBusiness(eq(1L), any(Company.class));
    }
    
    @Test
    void testUpdateBusinessNotFound() throws Exception {
        Company updatedCompany = new Company("Updated Corp", "456 New St");
        when(businessService.updateBusiness(eq(999L), any(Company.class)))
                .thenThrow(new RuntimeException("Business not found with id: 999"));
        
        String requestBody = objectMapper.writeValueAsString(updatedCompany);
        
        mockMvc.perform(put("/api/businesses/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
        
        verify(businessService, times(1)).updateBusiness(eq(999L), any(Company.class));
    }
    
    @Test
    void testDeleteBusiness() throws Exception {
        doNothing().when(businessService).deleteBusiness(1L);
        
        mockMvc.perform(delete("/api/businesses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Business deleted successfully"));
        
        verify(businessService, times(1)).deleteBusiness(1L);
    }
    
    @Test
    void testDeleteBusinessNotFound() throws Exception {
        doThrow(new RuntimeException("Business not found with id: 999"))
                .when(businessService).deleteBusiness(999L);
        
        mockMvc.perform(delete("/api/businesses/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
        
        verify(businessService, times(1)).deleteBusiness(999L);
    }
    
    @Test
    void testGetAllBusinesses() throws Exception {
        Company company2 = new Company("Another Corp", "789 Second St");
        company2.setId(2L);
        List<Company> companies = Arrays.asList(testCompany, company2);
        
        when(businessService.getAllBusinesses()).thenReturn(companies);
        
        mockMvc.perform(get("/api/businesses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.data[0].name").value("Test Corp"))
                .andExpect(jsonPath("$.data[1].name").value("Another Corp"));
        
        verify(businessService, times(1)).getAllBusinesses();
    }
}

