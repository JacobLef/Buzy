package app.training;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.training.dto.CreateTrainingRequest;
import app.training.dto.TrainingDTO;
import app.training.dto.UpdateTrainingRequest;

@RestController
@RequestMapping("/api/training")
public class TrainingController {

  private final TrainingService trainingService;

  public TrainingController(TrainingService trainingService) {
    this.trainingService = trainingService;
  }

  @PostMapping("/person/{personId}")
  public ResponseEntity<TrainingDTO> addTraining(
      @PathVariable Long personId, @RequestBody CreateTrainingRequest request) {
    TrainingDTO created = trainingService.addTraining(personId, request);
    return ResponseEntity.status(201).body(created);
  }

  @GetMapping("/person/{personId}")
  public ResponseEntity<List<TrainingDTO>> getTrainingsByPerson(@PathVariable Long personId) {
    List<TrainingDTO> trainings = trainingService.getTrainingsByPerson(personId);
    return ResponseEntity.ok(trainings);
  }

  @GetMapping("/person/{personId}/expired")
  public ResponseEntity<List<TrainingDTO>> getExpiredTrainings(@PathVariable Long personId) {
    List<TrainingDTO> trainings = trainingService.getExpiredTrainings(personId);
    return ResponseEntity.ok(trainings);
  }

  @GetMapping("/{trainingId}")
  public ResponseEntity<TrainingDTO> getTrainingById(@PathVariable Long trainingId) {
    TrainingDTO training = trainingService.getTrainingById(trainingId);
    return ResponseEntity.ok(training);
  }

  @PutMapping("/{trainingId}")
  public ResponseEntity<TrainingDTO> updateTraining(
      @PathVariable Long trainingId, @RequestBody UpdateTrainingRequest request) {
    TrainingDTO updated = trainingService.updateTraining(trainingId, request);
    return ResponseEntity.ok(updated);
  }

  @DeleteMapping("/{trainingId}")
  public ResponseEntity<Void> deleteTraining(@PathVariable Long trainingId) {
    trainingService.deleteTraining(trainingId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<TrainingDTO>> getAllTrainings() {
    List<TrainingDTO> trainings = trainingService.getAllTrainings();
    return ResponseEntity.ok(trainings);
  }
}
