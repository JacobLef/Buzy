package edu.neu.csye6200.repository;

import edu.neu.csye6200.model.domain.BusinessPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessPersonRepository extends JpaRepository<BusinessPerson, Long> { }