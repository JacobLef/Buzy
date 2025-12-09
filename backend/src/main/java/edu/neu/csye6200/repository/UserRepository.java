package edu.neu.csye6200.repository;

import edu.neu.csye6200.model.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email (for login).
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if email exists.
     * More efficient than findByEmail().isPresent().
     */
    boolean existsByEmail(String email);

    /**
     * Find user by business person ID.
     * Used to check if BusinessPerson already has a User account.
     */
    Optional<User> findByBusinessPersonId(Long businessPersonId);
}