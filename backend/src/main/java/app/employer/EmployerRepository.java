package app.employer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {
  List<Employer> findByCompanyId(Long companyId);

  List<Employer> findByDepartment(String department);
}
