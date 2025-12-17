package app.employee;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  List<Employee> findByCompanyId(Long companyId);

  @Query("SELECT e FROM Employee e WHERE e.manager.id = :managerId")
  List<Employee> findByManagerId(@Param("managerId") Long managerId);

  List<Employee> findByPosition(String position);

  List<Employee> findByNameContainingIgnoreCase(String name);

  List<Employee> findByHireDateAfter(LocalDate date);

  List<Employee> findByHireDateBefore(LocalDate date);

  List<Employee> findByHireDateBetween(LocalDate startDate, LocalDate endDate);

  @Query("SELECT e FROM Employee e WHERE e.manager IS NULL")
  List<Employee> findEmployeesWithoutManager();

  @Query("SELECT e FROM Employee e WHERE e.company.id = :companyId AND e.status = 'Active'")
  List<Employee> findActiveEmployeesByBusiness(@Param("companyId") Long companyId);

  /** Find employees hired after a date for a specific business */
  @Query("SELECT e FROM Employee e WHERE e.company.id = :companyId AND e.hireDate >= :date")
  List<Employee> findByCompanyIdAndHireDateAfter(
      @Param("companyId") Long companyId, @Param("date") LocalDate date);
}
