package app.business;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessPersonRepository extends JpaRepository<BusinessPerson, Long> {}
