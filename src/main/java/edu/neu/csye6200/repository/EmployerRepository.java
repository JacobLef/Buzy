package edu.neu.csye6200.repository;

import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {
  List<Employer> findByBusinessId(Long businessId);

  List<Employer> findByNameContainingIgnoreCase(String name);

  List<Employer> findByEmail(String email);

  List<Employer> findByStatus(String status);

  List<Employer> findBySalaryBetween(Double minSalary, Double maxSalary);

  List<Employer> findBySalary(Double salary);

  List<Employer> findByDepartment(String department);

  List<Employer> findByTitle(String title);

  List<Employer> findByHireDateAfter(LocalDate date);

  List<Employer> findByHireDateBefore(LocalDate date);

  List<Employer> findByHireDateBetween(LocalDate startDate, LocalDate endDate);

  @Query("SELECT e FROM Employee e WHERE e.manager = :manager_id")
  List<Employee> findAllManagedEmployees(@Param("manager_id") Long managerId);
}
