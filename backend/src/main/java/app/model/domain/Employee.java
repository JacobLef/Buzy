package app.model.domain;

import jakarta.persistence.*;

/**
 * Employee entity representing an employee within a business.
 */
@Entity
@Table(name = "employee")
@DiscriminatorValue("EMPLOYEE")
public class Employee extends BusinessPerson {

  @Column(nullable = false)
  private String position;

  @ManyToOne
  @JoinColumn(name = "manager_id")
  private BusinessPerson manager;

  public Employee() {
    super();
  }

  public Employee(String name, String email, String password, Double salary, String position) {
    super(name, email, password, salary);
    this.position = position;
  }

  @Override
  public String getPersonType() {
    return "Employee";
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public BusinessPerson getManager() {
    return manager;
  }

  public void setManager(BusinessPerson manager) {
    this.manager = manager;
  }

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
        ", salary=" + getSalary() +
        ", hireDate=" + getHireDate() +
        ", manager=" + (manager != null ? manager.getName() : "None") +
        ", status='" + getStatus() + '\'' +
        '}';
  }
}