package edu.neu.csye6200.dto;

import edu.neu.csye6200.model.domain.PersonStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployerDTO {

  private final Long id;
  private final String name;
  private final String email;
  private final PersonStatus status;
  private final Double salary;
  private final LocalDate hireDate;
  private final Long companyId;
  private final String companyName;
  private final String department;
  private final String title;
  private final Integer directReportsCount;
  private final Boolean isAdmin;
  private final Boolean isOwner;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  private EmployerDTO(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.email = builder.email;
    this.status = builder.status;
    this.salary = builder.salary;
    this.hireDate = builder.hireDate;
    this.companyId = builder.companyId;
    this.companyName = builder.companyName;
    this.department = builder.department;
    this.title = builder.title;
    this.directReportsCount = builder.directReportsCount;
    this.isAdmin = builder.isAdmin;
    this.isOwner = builder.isOwner;
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

  public String getDepartment() {
    return department;
  }

  public String getTitle() {
    return title;
  }

  public Integer getDirectReportsCount() {
    return directReportsCount;
  }

  public Boolean getIsAdmin() {
    return isAdmin != null ? isAdmin : false;
  }

  public Boolean getIsOwner() {
    return isOwner != null ? isOwner : false;
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
    private String department;
    private String title;
    private Integer directReportsCount;
    private Boolean isAdmin;
    private Boolean isOwner;
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

    public Builder withDepartment(String department) {
      this.department = department;
      return this;
    }

    public Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder withDirectReportsCount(Integer directReportsCount) {
      this.directReportsCount = directReportsCount;
      return this;
    }

    public Builder withIsAdmin(Boolean isAdmin) {
      this.isAdmin = isAdmin;
      return this;
    }

    public Builder withIsOwner(Boolean isOwner) {
      this.isOwner = isOwner;
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

    public EmployerDTO build() {
      return new EmployerDTO(this);
    }
  }
}