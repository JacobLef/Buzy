package app.business;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Company (Business) entity
 *
 * @author Qing Mi
 */
@Repository
public interface BusinessRepository extends JpaRepository<Company, Long> {

  /** Find company by name */
  Optional<Company> findByName(String name);

  /** Check if company exists by name */
  boolean existsByName(String name);
}
