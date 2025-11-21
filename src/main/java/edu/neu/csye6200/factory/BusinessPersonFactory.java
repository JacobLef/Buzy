package edu.neu.csye6200.factory;

import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;

/**
 * A factory for BusinessPerson objects, such that it permits the creation of employees and
 * employers, with the goal of abstracting away the construction process of these objects.
 */
public class BusinessPersonFactory {
  /**
   * Create an Employee with respect to the provided parameters (excluding a manager).
   */
  public static Employee createEmployee(
      String name,
      String email,
      String password,
      Double salary,
      String position
  ) {
    return new Employee(name, email, password, salary, position);
  }

  /**
   * Create an Employee with manager.
   */
  public static Employee createEmployee(
      String name,
      String email,
      String password,
      Double salary,
      String position,
      Employer manager
  ) {
    Employee employee = new Employee(name, email, password, salary, position);
    employee.setManager(manager);
    if (manager != null) {
      manager.addManagedEmployee(employee);
    }
    return employee;
  }

  /**
   * Create an Employer.
   */
  public static Employer createEmployer(
      String name,
      String email,
      String password,
      String department,
      String title
  ) {
    return new Employer(name, email, password, department, title);
  }
}
