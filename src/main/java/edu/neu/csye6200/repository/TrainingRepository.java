package edu.neu.csye6200.repository;

import edu.neu.csye6200.model.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    /**
     * Find all trainings for a specific person (Employee or Employer).
     */
    List<Training> findByPersonId(Long personId);

    /**
     * Find all expired trainings for a specific person.
     */
    List<Training> findByPersonIdAndExpiryDateBefore(Long personId, LocalDate today);

    /**
     * Find all trainings expiring within a date range.
     */
    List<Training> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find all required trainings for a specific person.
     */
    List<Training> findByPersonIdAndRequiredTrue(Long personId);
}