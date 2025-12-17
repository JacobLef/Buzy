package app.business;

import app.business.dto.CompanyDTO;
import app.business.dto.CreateBusinessRequest;
import app.business.dto.UpdateBusinessRequest;
import app.common.factory.DTOFactory;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** REST Controller for business management operations */
@RestController
@RequestMapping("/api/businesses")
public class BusinessController {

  private final BusinessService businessService;
  private final DTOFactory dtoFactory;

  @Autowired
  public BusinessController(BusinessService businessService, DTOFactory dtoFactory) {
    this.businessService = businessService;
    this.dtoFactory = dtoFactory;
  }

  // Spring boot will automatically map the request body to the
  // CreateBusinessRequest object
  // example: POST /api/businesses HTTP/1.1
  // Content-Type: application/json
  // {
  // "name": "Acme Inc.",
  // "address": "123 Main St, Anytown, USA",
  // "industry": "Technology",
  // "foundedDate": "2020-01-01"
  // }
  @PostMapping
  public ResponseEntity<CompanyDTO> createBusiness(@RequestBody CreateBusinessRequest request) {
    Company createdBusiness = businessService.createBusiness(request);
    CompanyDTO dto = dtoFactory.createDTO(createdBusiness);
    return new ResponseEntity<>(dto, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CompanyDTO> updateBusiness(@PathVariable Long id,
      @RequestBody UpdateBusinessRequest request) {
    Company updatedBusiness = businessService.updateBusiness(id, request);
    CompanyDTO dto = dtoFactory.createDTO(updatedBusiness);
    return ResponseEntity.ok(dto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteBusiness(@PathVariable Long id) {
    businessService.deleteBusiness(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<CompanyDTO> getBusiness(@PathVariable Long id) {
    Company business = businessService.getBusiness(id);
    CompanyDTO dto = dtoFactory.createDTO(business);
    return ResponseEntity.ok(dto);
  }

  @GetMapping
  public ResponseEntity<List<CompanyDTO>> getAllBusinesses() {
    List<Company> businesses = businessService.getAllBusinesses();
    List<CompanyDTO> dtos = businesses.stream().map(dtoFactory::createDTO)
        .collect(Collectors.toList());
    return ResponseEntity.ok(dtos);
  }
}
