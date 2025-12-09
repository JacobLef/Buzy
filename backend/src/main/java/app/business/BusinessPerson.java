package app.business;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import app.user.PersonStatus;

/**
 * Abstract base class for all company persons ({@link Employee} and
 * {@link Employer}).
 * Uses JOINED inheritance - each subclass has its own table with a foreign key
 * to this table.
 */
@Entity
@Table(name = "business_person")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "person_type", discriminatorType = DiscriminatorType.STRING)
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

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  protected PersonStatus status;

  @Column(nullable = false)
  protected Double salary;

  @Column(name = "hire_date")
  protected LocalDate hireDate;

  @Column(name = "created_at", updatable = false)
  protected LocalDateTime createdAt;

  @Column(name = "updated_at")
  protected LocalDateTime updatedAt;

  protected BusinessPerson() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.status = PersonStatus.Active;
    this.hireDate = LocalDate.now();
  }

  protected BusinessPerson(String name, String email, String password, Double salary) {
    this();
    this.name = name;
    this.email = email;
    this.password = password;
    this.salary = salary;
  }

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

  public Double getSalary() {
    return salary;
  }

  public void setSalary(Double salary) {
    this.salary = salary;
    this.updatedAt = LocalDateTime.now();
  }

  public LocalDate getHireDate() {
    return hireDate;
  }

  public void setHireDate(LocalDate hireDate) {
    this.hireDate = hireDate;
    this.updatedAt = LocalDateTime.now();
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public int getYearsOfService() {
    if (hireDate == null) {
      return 0;
    }
    return LocalDate.now().getYear() - hireDate.getYear();
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
        ", salary=" + salary +
        ", hireDate=" + hireDate +
        ", status='" + status + '\'' +
        '}';
  }
}