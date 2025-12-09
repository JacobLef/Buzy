package app.dto;

import app.model.domain.PersonStatus;

import java.time.LocalDateTime;

/**
 * Abstract DTO base class for BusinessPerson data transfer.
 * Contains common fields shared by EmployeeDTO and EmployerDTO.
 * Uses Builder pattern for object construction.
 */
public abstract class BusinessPersonDTO {
  private final Long id;
  private final String name;
  private final String email;
  private final Long businessId;
  private final String businessName;
  private final PersonStatus status;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  protected BusinessPersonDTO(Builder<?> builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.email = builder.email;
    this.businessId = builder.businessId;
    this.businessName = builder.businessName;
    this.status = builder.status;
    this.createdAt = builder.createdAt;
    this.updatedAt = builder.updatedAt;
  }

  /**
   * Get the type of business person (Employee or Employer).
   */
  public abstract String getPersonType();

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public Long getBusinessId() {
    return businessId;
  }

  public String getBusinessName() {
    return businessName;
  }

  public PersonStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }


  /**
   * Abstract builder class for BusinessPersonDTO.
   */
  public abstract static class Builder<T extends Builder<T>> {
    private Long id;
    private String name;
    private String email;
    private Long businessId;
    private String businessName;
    private PersonStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Return this builder.
     * @return the type of this Builder.
     */
    protected abstract T self();

    public T withId(Long id) {
      this.id = id;
      return self();
    }

    public T withName(String name) {
      this.name = name;
      return self();
    }

    public T withEmail(String email) {
      this.email = email;
      return self();
    }

    public T withBusinessId(Long businessId) {
      this.businessId = businessId;
      return self();
    }

    public T withBusinessName(String businessName) {
      this.businessName = businessName;
      return self();
    }

    public T withStatus(PersonStatus status) {
      this.status = status;
      return self();
    }

    public T createdAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return self();
    }

    public T updatedAt(LocalDateTime updatedAt) {
      this.updatedAt = updatedAt;
      return self();
    }

    public abstract BusinessPersonDTO build();
  }
}