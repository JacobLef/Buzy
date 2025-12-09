package app.repository;

import app.model.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Company (Business) entity
 * 
 * @author Qing Mi
 */
@Repository
public interface BusinessRepository extends JpaRepository<Company, Long> {
    
    /**
     * Find company by name
     */
    Optional<Company> findByName(String name);
    
    /**
     * Check if company exists by name
     */
    boolean existsByName(String name);
}