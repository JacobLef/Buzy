package edu.neu.csye6200.repository;


import edu.neu.csye6200.model.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    List<Training> findByPersonId(Long personId);

    @Query("SELECT t FROM Training t WHERE t.expiryDate < :today")
    List<Training> findExpiredTrainings(LocalDate today);
}
