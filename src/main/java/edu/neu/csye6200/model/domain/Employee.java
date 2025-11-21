package edu.neu.csye6200.model.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Employee entity representing an employee within a business.
 */
@Entity
@Table(name = "employee")
public class Employee extends BusinessPerson {
  @Column(nullable = false)
  private Double salary;

  @ManyToOne
  @JoinColumn(name = "manager_id")
  private Employer manager;

  @Column(name = "hire_date")
  private LocalDate hireDate;

  @Column(nullable = false)
  private String position;

  public Employee() {
    super();
    this.hireDate = LocalDate.now();
  }

  public Employee(String name, String email, String password, Double salary, String position) {
    super(name, email, password);
    this.salary = salary;
    this.position = position;
    this.hireDate = LocalDate.now();
  }

  @Override
  public String getPersonType() {
    return "Employee";
  }

  public Double getSalary() {
    return salary;
  }

  public void setSalary(Double salary) {
    this.salary = salary;
  }

  public Employer getManager() {
    return this.manager;
  }

  public void setManager(Employer manager) {
    this.manager = manager;
  }

  public LocalDate getHireDate() {
    return hireDate;
  }

  public void setHireDate(LocalDate hireDate) {
    this.hireDate = hireDate;
  }

  public String getPosition() {
    return this.position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  /**
   * Calculate the number of years for which this Employee has worked at their company.
   * @return the integer representation of this calculation.
   */
  public int getYearsOfService() {
    return LocalDate.now().getYear() - hireDate.getYear();
  }

  /**
   * Determines if this Employee has a manager.
   * @return true if it is the case and false otherwise.
   */
  public boolean hasManager() {
    return manager != null;
  }

  @Override
  public String toString() {
    return "Employee{" +
        "id=" + getId() +
        ", name='" + getName() + '\'' +
        ", email='" + getEmail() + '\'' +
        ", position='" + position + '\'' +
        ", salary=" + salary +
        ", hireDate=" + hireDate +
        ", manager=" + (manager != null ? manager.getName() : "None") +
        ", status='" + getStatus() + '\'' +
        '}';
  }
}
