package edu.neu.csye6200.service.interfaces;

import edu.neu.csye6200.dto.EmployeeDTO;
import edu.neu.csye6200.dto.request.CreateEmployeeRequest;
import edu.neu.csye6200.dto.request.UpdateEmployeeRequest;
import edu.neu.csye6200.model.domain.Employee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for employee management operations
 * 
 * @author Team 10
 */
@Service
public interface EmployeeService {
  /**
   * Creates a new employee.
   * @param req Data for the new employee.
   * @return Created Employee.
   */
  Employee createEmployee(CreateEmployeeRequest req);

  /**
   * Fetches the employee mapped to the provided ID.
   * @param id the id of the employee to be fetched.
   * @return {@code Optional.empty()} if there is no employee by the id provided or the employee
   * object that relates to the given id.
   */
  Optional<Employee> getEmployee(Long id);

  /**
   * Fetches all employees.
   * @return a list of the employees to be fetched.
   */
  List<Employee> getAllEmployees();

  /**
   * Updates an existing employee with the given data.
   * @param id the id of the employee to be updated.
   * @param req employee data.
   * @return the new Updated employee.
   */
  Employee updateEmployee(Long id, UpdateEmployeeRequest req);

  /**
   * Delete an employee.
   * @param id the id of the Employee to be deleted.
   */
  void deleteEmployee(Long id);

  /**
   * Get all employees for a specific business;
   * @param businessId the id of the business whose employees are to be fetched.
   * @return the list of employees working at that business.
   */
  List<Employee> getEmployeesByBusiness(Long businessId);

  /**
   * Get all employees managed by a specific employer.
   * @param managerId the id of the manager whose managees are to be fetched.
   * @return a list of the employees found.
   */
  List<Employee> getEmployeesByManager(Long managerId);

  /**
   * Assign a manager to an employee
   * @param employeeId Employee ID
   * @param managerId Manager/Employer ID
   * @return Updated employee
   */
  Employee assignManager(Long employeeId, Long managerId);

  /**
   * Remove manager from an employee
   * @param employeeId Employee ID
   * @return Updated employee
   */
  Employee removeManager(Long employeeId);

  /**
   * Update employee salary
   * @param employeeId Employee ID
   * @param newSalary New salary amount
   * @return Updated employee
   */
  Employee updateSalary(Long employeeId, Double newSalary);

  /**
   * Give bonus to employee
   * @param employeeId Employee ID
   * @param bonusAmount Bonus amount
   * @return Updated employee
   */
  Employee giveBonus(Long employeeId, Double bonusAmount);


  /**
   * Update employee position (promotion/demotion)
   * @param employeeId Employee ID
   * @param newPosition New position title
   * @return Updated employee
   */
  Employee updatePosition(Long employeeId, String newPosition);

  /**
   * Update employee status (Active, Inactive, On Leave, etc.)
   * @param employeeId Employee ID
   * @param status New status
   * @return Updated employee
   */
  Employee updateStatus(Long employeeId, String status);


  /**
   * Search employees by name
   * @param name Name to search for
   * @return List of matching employees
   */
  List<Employee> searchByName(String name);

  /**
   * Get employees by position
   * @param position Position title
   * @return List of employees with that position
   */
  List<Employee> getEmployeesByPosition(String position);

  /**
   * Get employees hired after a certain date
   * @param date Date in format YYYY-MM-DD
   * @return List of employees hired after this date
   */
  List<Employee> getEmployeesHiredAfter(String date);
}

