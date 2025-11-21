package edu.neu.csye6200.model.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Employer entity representing a manager/employer within a business.
 * Extends BusinessPerson with employer-specific fields.
 */
@Entity
@Table(name = "employer")
public class Employer extends BusinessPerson {

  @Column(nullable = false)
  private String department;

  @Column(nullable = false)
  private String title;

  @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
  private Set<Employee> managedEmployees;

  public Employer() {
    super();
    this.managedEmployees = new HashSet<>();
  }

  public Employer(String name, String email, String password, String department, String title) {
    super(name, email, password);
    this.department = department;
    this.title = title;
    this.managedEmployees = new HashSet<>();
  }

  @Override
  public String getPersonType() {
    return "Employer";
  }

  public String getDepartment() {
    return department;
  }

  public void setDepartment(String department) {
    this.department = department;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Set<Employee> getManagedEmployees() {
    return managedEmployees;
  }

  public void setManagedEmployees(Set<Employee> managedEmployees) {
    this.managedEmployees = managedEmployees;
  }

  /**
   * Add an employee to this employer's managed list.
   * @param employee the employer to add to be managed by this Employer. If this employee already
   *                exists, then it is not added.
   */
  public void addManagedEmployee(Employee employee) {
    if (!this.managedEmployees.add(employee)) {
      employee.setManager(this);
    }
  }

  /**
   * Remove an employee from this employer's managed list.
   * @param employee the employee to be removed under this employer.
   */
  public void removeManagedEmployee(Employee employee) {
    if (managedEmployees.contains(employee)) {
      managedEmployees.remove(employee);
      employee.setManager(null);
    }
  }

  /**
   * Get count of direct reports to this Employer (count number of employees).
   * @return an integer representation of this count.
   */
  public int getDirectReportsCount() {
    return managedEmployees != null ? managedEmployees.size() : 0;
  }

  /**
   * Check if this employer manages any employees.
   * @return true if this Employer has at least one employer directly underneath them and,
   * otherwise, return false.
   */
  public boolean hasDirectReports() {
    return managedEmployees != null && !managedEmployees.isEmpty();
  }

  /**
   * Check if this employer manages a specific employee.
   * @param employee the employee to check if it is managed by this Employer.
   * @return true if this employer managers the given employee and false otherwise.
   */
  public boolean managesEmployee(Employee employee) {
    return managedEmployees != null && managedEmployees.contains(employee);
  }

  @Override
  public String toString() {
    return "Employer{" +
        "id=" + getId() +
        ", name='" + getName() + '\'' +
        ", email='" + getEmail() + '\'' +
        ", department='" + department + '\'' +
        ", title='" + title + '\'' +
        ", directReports=" + getDirectReportsCount() +
        ", status='" + getStatus() + '\'' +
        '}';
  }
}