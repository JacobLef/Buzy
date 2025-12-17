package app.employer;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

import app.business.BusinessPerson;
import app.employee.Employee;

/**
 * Employer entity representing a manager/employer within a business.
 */
@Entity
@Table(name = "employer")
@DiscriminatorValue("EMPLOYER")
public class Employer extends BusinessPerson {

  @Column(nullable = false)
  private String department;

  @Column(nullable = false)
  private String title;

  @Column(name = "is_admin")
  private Boolean isAdmin = false;

  @Column(name = "is_owner")
  private Boolean isOwner = false;

  @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL)
  private Set<Employee> managedEmployees;

  public Employer() {
    super();
    this.managedEmployees = new HashSet<>();
  }

  public Employer(String name, String email, String password, Double salary, String department,
      String title) {
    super(name, email, password, salary);
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

  public void addManagedEmployee(Employee employee) {
    if (this.managedEmployees.add(employee)) {
      employee.setManager(this);
    }
  }

  public void removeManagedEmployee(Employee employee) {
    if (managedEmployees.remove(employee)) {
      employee.setManager(null);
    }
  }

  public int getDirectReportsCount() {
    return managedEmployees != null ? managedEmployees.size() : 0;
  }

  public boolean hasDirectReports() {
    return managedEmployees != null && !managedEmployees.isEmpty();
  }

  public boolean managesEmployee(Employee employee) {
    return managedEmployees != null && managedEmployees.contains(employee);
  }

  public Boolean getIsAdmin() {
    return isAdmin != null ? isAdmin : false;
  }

  public void setIsAdmin(Boolean isAdmin) {
    this.isAdmin = isAdmin != null ? isAdmin : false;
  }

  public Boolean getIsOwner() {
    return isOwner != null ? isOwner : false;
  }

  public void setIsOwner(Boolean isOwner) {
    this.isOwner = isOwner != null ? isOwner : false;
  }

  @Override
  public String toString() {
    return "Employer{" + "id=" + getId() + ", name='" + getName() + '\'' + ", email='" + getEmail()
        + '\'' + ", department='" + department + '\'' + ", title='" + title + '\'' + ", salary="
        + getSalary() + ", hireDate=" + getHireDate() + ", directReports=" + getDirectReportsCount()
        + ", status='" + getStatus() + '\'' + '}';
  }
}
