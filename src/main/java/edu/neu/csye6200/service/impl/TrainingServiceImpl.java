package edu.neu.csye6200.service.impl;

import edu.neu.csye6200.dto.TrainingDTO;
import edu.neu.csye6200.model.domain.BusinessPerson;
import edu.neu.csye6200.model.domain.Employee;
import edu.neu.csye6200.model.domain.Training;
import edu.neu.csye6200.repository.EmployeeRepository;
import edu.neu.csye6200.repository.TrainingRepository;
import edu.neu.csye6200.service.interfaces.TrainingService;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingServiceImpl implements TrainingService {

  private final TrainingRepository trainingRepository;
  private final EmployeeRepository employeeRepository;

  public TrainingServiceImpl(
      TrainingRepository trainingRepository,
      EmployeeRepository employeeRepository
  ) {
    this.trainingRepository = trainingRepository;
    this.employeeRepository = employeeRepository;
  }


  @Override
  public TrainingDTO addTraining(Long employeeId, TrainingDTO dto) {

    Employee employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new RuntimeException("Employee not found."));

    Training training = new Training(
        dto.getTrainingName(),
        dto.getDescription(),
        dto.getCompletionDate(),
        dto.getExpiryDate(),
        dto.isRequired()
    );

    training.setPerson(employee);

    if (training.getCompletionDate() != null) {
      training.setExpiryDate(training.getCompletionDate().plusYears(1));
    }

    Training saved = trainingRepository.save(training);

    updateTrainingExpiryStatusForPerson(employeeId);

    return convertToDTO(saved, employee);
  }


  @Override
  public List<TrainingDTO> getTrainingsByPerson(Long personId) {

    List<Training> trainings = trainingRepository.findByPersonId(personId);

    updateTrainingExpiryStatusForPerson(personId);

    return trainings.stream()
        .map(t -> convertToDTO(t, t.getPerson()))
        .collect(Collectors.toList());
  }


  @Override
  public List<TrainingDTO> checkExpiredTrainings(Long personId) {

    List<Training> trainings = trainingRepository.findByPersonId(personId);

    updateTrainingExpiryStatusForPerson(personId);

    return trainings.stream()
        .map(t -> convertToDTO(t, t.getPerson()))
        .collect(Collectors.toList());
  }


  private void updateTrainingExpiryStatusForPerson(Long personId) {

    List<Training> trainings = trainingRepository.findByPersonId(personId);
    LocalDate today = LocalDate.now();
//check all training table in Person
//if training.expiryDate < today → labeled expired
//return labeled "expired" TrainingDTO
    //instead of findExpiredTrainings()
    for (Training t : trainings) {
      if (t.getExpiryDate() != null && t.getExpiryDate().isBefore(today)) {
        t.setExpired(true);
      }
    }

    trainingRepository.saveAll(trainings);
  }

  private TrainingDTO convertToDTO(Training t, BusinessPerson person) {

    return TrainingDTO.builder()
        .withId(t.getId())
        .withTrainingName(t.getTrainingName())
        .withDescription(t.getDescription())
        .withCompletionDate(t.getCompletionDate())
        .withExpiryDate(t.getExpiryDate())
        .withRequired(t.isRequired())
        .withExpired(t.isExpired())
        .withPersonId(person.getId())
        .withPersonName(person.getName())
        .withCreatedAt(t.getCreatedAt())
        .build();
  }
}
