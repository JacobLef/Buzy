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

  List<Employee> findByNameContainingIgnoreCase(String name);

  List<Employee> findByHireDateAfter(LocalDate date);
}