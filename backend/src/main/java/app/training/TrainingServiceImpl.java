package app.training;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.business.BusinessPerson;
import app.business.BusinessPersonRepository;
import app.common.exception.ResourceNotFoundException;
import app.training.dto.CreateTrainingRequest;
import app.training.dto.TrainingDTO;
import app.training.dto.UpdateTrainingRequest;

@Service
@Transactional
public class TrainingServiceImpl implements TrainingService {

  private final TrainingRepository trainingRepository;
  private final BusinessPersonRepository businessPersonRepository;

  public TrainingServiceImpl(TrainingRepository trainingRepository,
      BusinessPersonRepository businessPersonRepository) {
    this.trainingRepository = trainingRepository;
    this.businessPersonRepository = businessPersonRepository;
  }

  @Override
  public TrainingDTO addTraining(Long personId, CreateTrainingRequest request) {
    BusinessPerson person = businessPersonRepository.findById(personId)
        .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
    Training training = createTrainingFromRequest(request);
    training.setPerson(person);
    Training saved = trainingRepository.save(training);
    return convertToDTO(saved);
  }

  private Training createTrainingFromRequest(CreateTrainingRequest request) {
    return new Training(request.trainingName(), request.description(), request.completionDate(),
        request.expiryDate(), request.required());
  }

  @Override
  @Transactional(readOnly = true)
  public List<TrainingDTO> getTrainingsByPerson(Long personId) {
    if (!businessPersonRepository.existsById(personId)) {
      throw new ResourceNotFoundException("Person", "id", personId);
    }

    return trainingRepository.findByPersonId(personId).stream().map(this::convertToDTO).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<TrainingDTO> getExpiredTrainings(Long personId) {
    if (!businessPersonRepository.existsById(personId)) {
      throw new ResourceNotFoundException("Person", "id", personId);
    }

    return trainingRepository.findByPersonId(personId).stream().filter(Training::isExpired)
        .map(this::convertToDTO).toList();
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
    return trainingRepository.findAll().stream().map(this::convertToDTO).toList();
  }

  private TrainingDTO convertToDTO(Training training) {
    TrainingDTO.Builder builder = TrainingDTO.builder().withId(training.getId())
        .withTrainingName(training.getTrainingName()).withDescription(training.getDescription())
        .withCompletionDate(training.getCompletionDate()).withExpiryDate(training.getExpiryDate())
        .withRequired(training.isRequired()).withCompleted(training.isCompleted())
        .withExpired(training.isExpired()).withCreatedAt(training.getCreatedAt());

    if (training.getPerson() != null) {
      builder.withPersonId(training.getPerson().getId())
          .withPersonName(training.getPerson().getName())
          .withPersonType(training.getPerson().getPersonType());
    }

    return builder.build();
  }
}
