package edu.neu.csye6200.repository;

import edu.neu.csye6200.model.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Business entity operations
 */
@Repository
public interface BusinessRepository extends JpaRepository<Company, Long> {
}

