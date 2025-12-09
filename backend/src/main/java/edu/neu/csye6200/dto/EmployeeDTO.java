package edu.neu.csye6200.dto;

import edu.neu.csye6200.model.domain.PersonStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeDTO {

  private final Long id;
  private final String name;
  private final String email;
  private final PersonStatus status;
  private final Double salary;
  private final LocalDate hireDate;
  private final Long companyId;
  private final String companyName;
  private final String position;
  private final Long managerId;
  private final String managerName;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  private EmployeeDTO(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.email = builder.email;
    this.status = builder.status;
    this.salary = builder.salary;
    this.hireDate = builder.hireDate;
    this.companyId = builder.companyId;
    this.companyName = builder.companyName;
    this.position = builder.position;
    this.managerId = builder.managerId;
    this.managerName = builder.managerName;
    this.createdAt = builder.createdAt;
    this.updatedAt = builder.updatedAt;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public PersonStatus getStatus() {
    return status;
  }

  public Double getSalary() {
    return salary;
  }

  public LocalDate getHireDate() {
    return hireDate;
  }

  public Long getCompanyId() {
    return companyId;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getPosition() {
    return position;
  }

  public Long getManagerId() {
    return managerId;
  }

  public String getManagerName() {
    return managerName;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Long id;
    private String name;
    private String email;
    private PersonStatus status;
    private Double salary;
    private LocalDate hireDate;
    private Long companyId;
    private String companyName;
    private String position;
    private Long managerId;
    private String managerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Builder withId(Long id) {
      this.id = id;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withEmail(String email) {
      this.email = email;
      return this;
    }

    public Builder withStatus(PersonStatus status) {
      this.status = status;
      return this;
    }

    public Builder withSalary(Double salary) {
      this.salary = salary;
      return this;
    }

    public Builder withHireDate(LocalDate hireDate) {
      this.hireDate = hireDate;
      return this;
    }

    public Builder withCompanyId(Long companyId) {
      this.companyId = companyId;
      return this;
    }

    public Builder withCompanyName(String companyName) {
      this.companyName = companyName;
      return this;
    }

    public Builder withPosition(String position) {
      this.position = position;
      return this;
    }

    public Builder withManagerId(Long managerId) {
      this.managerId = managerId;
      return this;
    }

    public Builder withManagerName(String managerName) {
      this.managerName = managerName;
      return this;
    }

    public Builder withCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder withUpdatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public EmployeeDTO build() {
      return new EmployeeDTO(this);
    }
  }
}