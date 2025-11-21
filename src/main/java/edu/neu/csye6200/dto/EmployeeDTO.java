package edu.neu.csye6200.dto;

import java.time.LocalDate;

/**
 * DTO for Employee data transfer.
 * Uses Builder pattern for construction.
 */
public class EmployeeDTO extends BusinessPersonDTO {
  private final Double salary;
  private final String position;
  private final LocalDate hireDate;
  private final Long managerId;
  private final String managerName;

  private EmployeeDTO(Builder builder) {
    super(builder);
    this.salary = builder.salary;
    this.position = builder.position;
    this.hireDate = builder.hireDate;
    this.managerId = builder.managerId;
    this.managerName = builder.managerName;
  }

  @Override
  public String getPersonType() {
    return "Employee";
  }

  public Double getSalary() {
    return salary;
  }

  public String getPosition() {
    return position;
  }

  public LocalDate getHireDate() {
    return hireDate;
  }

  public Long getManagerId() {
    return managerId;
  }

  public String getManagerName() {
    return managerName;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder extends BusinessPersonDTO.Builder<Builder> {
    private Double salary;
    private String position;
    private LocalDate hireDate;
    private Long managerId;
    private String managerName;

    @Override
    protected Builder self() {
      return this;
    }

    public Builder withSalary(Double salary) {
      this.salary = salary;
      return this;
    }

    public Builder withPosition(String position) {
      this.position = position;
      return this;
    }

    public Builder withHireDate(LocalDate hireDate) {
      this.hireDate = hireDate;
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

    @Override
    public EmployeeDTO build() {
      return new EmployeeDTO(this);
    }
  }
}