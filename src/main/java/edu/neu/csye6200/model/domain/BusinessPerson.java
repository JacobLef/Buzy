package edu.neu.csye6200.model.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Abstract base class for all company persons ({@link Employee} and {@link Employer}).
 * Contains common fields and methods shared by both types.
 */
@MappedSuperclass
public abstract class BusinessPerson {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @Column(nullable = false)
  protected String name;

  @Column(nullable = false, unique = true)
  protected String email;

  @Column(nullable = false)
  protected String password;

  @ManyToOne
  @JoinColumn(name = "company_id")
  protected Company company;

  @Column(nullable = false)
  protected PersonStatus status;

  @Column(name = "created_at")
  protected final LocalDateTime createdAt;

  @Column(name = "updated_at")
  protected LocalDateTime updatedAt;

  public BusinessPerson() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.status = PersonStatus.Active;
  }

  public BusinessPerson(String name, String email, String password) {
    this();
    this.name = name;
    this.email = email;
    this.password = password;
  }

  /**
   * Abstract method that subclasses must implement such that they can define their specific type
   * (that is, as an Employee or Employer).
   * @return "Employee" or "Employer".
   */
  public abstract String getPersonType();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    this.updatedAt = LocalDateTime.now();
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
    this.updatedAt = LocalDateTime.now();
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
    this.updatedAt = LocalDateTime.now();
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
    this.updatedAt = LocalDateTime.now();
  }

  public PersonStatus getStatus() {
    return status;
  }

  public void setStatus(PersonStatus status) {
    this.status = status;
    this.updatedAt = LocalDateTime.now();
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  @Override
  public String toString() {
    return getPersonType() + "{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", status='" + status + '\'' +
        '}';
  }
}
