package app.employer;

import app.employer.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {
  List<Employer> findByCompanyId(Long companyId);

  List<Employer> findByDepartment(String department);
}