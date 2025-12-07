package edu.neu.csye6200.service.impl;

import edu.neu.csye6200.dto.TrainingDTO;
import edu.neu.csye6200.dto.request.CreateTrainingRequest;
import edu.neu.csye6200.exception.ResourceNotFoundException;
import edu.neu.csye6200.model.domain.BusinessPerson;
import edu.neu.csye6200.model.domain.Training;
import edu.neu.csye6200.repository.BusinessPersonRepository;
import edu.neu.csye6200.repository.TrainingRepository;
import edu.neu.csye6200.service.interfaces.TrainingService;
import edu.neu.csye6200.dto.request.UpdateTrainingRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {

  private final TrainingRepository trainingRepository;
  private final BusinessPersonRepository businessPersonRepository;

  public TrainingServiceImpl(
      TrainingRepository trainingRepository,
      BusinessPersonRepository businessPersonRepository
  ) {
    this.trainingRepository = trainingRepository;
    this.businessPersonRepository = businessPersonRepository;
  }

  @Override
  public TrainingDTO addTraining(Long personId, CreateTrainingRequest request) {
      BusinessPerson person = businessPersonRepository.findById(personId)
              .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
      Training training = createTrainingFromRequest(request);
    training.setPerson(person);
    setDefaultExpiryDate(training);

    Training saved = trainingRepository.save(training);
    return convertToDTO(saved);
  }

    private Training createTrainingFromRequest(CreateTrainingRequest request) {
        return new Training(
                request.trainingName(),
                request.description(),
                request.completionDate(),
                request.expiryDate(),
                request.required()
        );
    }

  @Override
  @Transactional(readOnly = true)
  public List<TrainingDTO> getTrainingsByPerson(Long personId) {
      if (!businessPersonRepository.existsById(personId)) {
          throw new ResourceNotFoundException("Person", "id", personId);
      }

    return trainingRepository.findByPersonId(personId).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public List<TrainingDTO> getExpiredTrainings(Long personId) {
      if (!businessPersonRepository.existsById(personId)) {
          throw new ResourceNotFoundException("Person", "id", personId);
      }

    return trainingRepository.findByPersonId(personId).stream()
        .filter(Training::isExpired)
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public TrainingDTO getTrainingById(Long trainingId) {
      Training training = trainingRepository.findById(trainingId)
              .orElseThrow(() -> new ResourceNotFoundException("Training", "id", trainingId));
    return convertToDTO(training);
  }

    @Override
    public TrainingDTO updateTraining(Long trainingId, UpdateTrainingRequest request) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new ResourceNotFoundException("Training", "id", trainingId));
        if (request.trainingName() != null) {
            training.setTrainingName(request.trainingName());
        }
        if (request.description() != null) {
            training.setDescription(request.description());
        }
        if (request.completionDate() != null) {
            training.setCompletionDate(request.completionDate());
        }
        if (request.expiryDate() != null) {
            training.setExpiryDate(request.expiryDate());
        }
        if (request.required() != null) {
            training.setRequired(request.required());
        }

        Training saved = trainingRepository.save(training);
        return convertToDTO(saved);
    }

  @Override
  public void deleteTraining(Long trainingId) {
      if (!trainingRepository.existsById(trainingId)) {
          throw new ResourceNotFoundException("Training", "id", trainingId);
      }

    trainingRepository.deleteById(trainingId);
  }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingDTO> getAllTrainings() {
        return trainingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

  // -------------------- Helper Methods --------------------

  private Training createTrainingFromDTO(TrainingDTO dto) {
    return new Training(
        dto.getTrainingName(),
        dto.getDescription(),
        dto.getCompletionDate(),
        dto.getExpiryDate(),
        dto.isRequired()
    );
  }

  private void setDefaultExpiryDate(Training training) {
    if (training.getCompletionDate() != null && training.getExpiryDate() == null) {
      training.setExpiryDate(training.getCompletionDate().plusYears(1));
    }
  }

  private TrainingDTO convertToDTO(Training training) {
    TrainingDTO.Builder builder = TrainingDTO.builder()
        .withId(training.getId())
        .withTrainingName(training.getTrainingName())
        .withDescription(training.getDescription())
        .withCompletionDate(training.getCompletionDate())
        .withExpiryDate(training.getExpiryDate())
        .withRequired(training.isRequired())
        .withExpired(training.isExpired())
        .withCreatedAt(training.getCreatedAt());

    if (training.getPerson() != null) {
      builder.withPersonId(training.getPerson().getId())
          .withPersonName(training.getPerson().getName())
          .withPersonType(training.getPerson().getPersonType());
    }

    return builder.build();
  }
}