package edu.neu.csye6200.repository;

import edu.neu.csye6200.model.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    /**
     * Find all trainings for a specific person (Employee or Employer).
     */
    @Query("SELECT t FROM Training t WHERE t.person.id = :personId")
    List<Training> findByPersonId(@Param("personId") Long personId);

    /**
     * Find all expired trainings for a specific person.
     */
    @Query("SELECT t FROM Training t WHERE t.person.id = :personId AND t.expiryDate < :today")
    List<Training> findByPersonIdAndExpiryDateBefore(@Param("personId") Long personId, @Param("today") LocalDate today);
    /**
     * Find all trainings expiring within a date range.
     */
    List<Training> findByExpiryDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find all required trainings for a specific person.
     */
    @Query("SELECT t FROM Training t WHERE t.person.id = :personId AND t.required = true")
    List<Training> findByPersonIdAndRequiredTrue(@Param("personId") Long personId);
}