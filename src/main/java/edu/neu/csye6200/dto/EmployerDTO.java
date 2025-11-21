package edu.neu.csye6200.dto;

/**
 * DTO for Employer data transfer.
 * Uses Builder pattern for construction.
 */
public class EmployerDTO extends BusinessPersonDTO {
  private final String department;
  private final String title;
  private final Integer directReportsCount;

  private EmployerDTO(Builder builder) {
    super(builder);
    this.department = builder.department;
    this.title = builder.title;
    this.directReportsCount = builder.directReportsCount;
  }

  @Override
  public String getPersonType() {
    return "Employer";
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

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder extends BusinessPersonDTO.Builder<Builder> {
    private String department;
    private String title;
    private Integer directReportsCount;

    @Override
    protected Builder self() {
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

    @Override
    public EmployerDTO build() {
      return new EmployerDTO(this);
    }
  }
}