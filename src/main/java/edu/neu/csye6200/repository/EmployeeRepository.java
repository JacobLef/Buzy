package edu.neu.csye6200.repository;

import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.PersonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  List<Employee> findByCompanyId(Long companyId);

  @Query("SELECT e FROM Employee e WHERE e.manager.id = :managerId")
  List<Employee> findByManagerId(@Param("managerId") Long managerId);

  List<Employee> findByPosition(String position);

  List<Employee> findByStatus(PersonStatus status);

  Optional<Employee> findByEmail(String email);

  List<Employee> findByNameContainingIgnoreCase(String name);

  List<Employee> findBySalaryBetween(Double minSalary, Double maxSalary);

  List<Employee> findBySalaryGreaterThan(Double salary);

  List<Employee> findByHireDateAfter(LocalDate date);

  List<Employee> findByHireDateBefore(LocalDate date);

  List<Employee> findByHireDateBetween(LocalDate startDate, LocalDate endDate);

  @Query("SELECT e FROM Employee e WHERE e.manager IS NULL")
  List<Employee> findEmployeesWithoutManager();

  @Query("SELECT e FROM Employee e WHERE e.company.id = :companyId AND e.status = 'Active'")
  List<Employee> findActiveEmployeesByBusiness(@Param("companyId") Long companyId);
  
  /**
   * Find employees hired after a date for a specific business
   */
  @Query("SELECT e FROM Employee e WHERE e.company.id = :companyId AND e.hireDate >= :date")
  List<Employee> findByCompanyIdAndHireDateAfter(
      @Param("companyId") Long companyId,
      @Param("date") LocalDate date
  );
}