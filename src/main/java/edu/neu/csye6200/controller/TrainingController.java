package edu.neu.csye6200.controller;

import edu.neu.csye6200.dto.TrainingDTO;
import edu.neu.csye6200.service.interfaces.TrainingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training")
public class TrainingController {

  private final TrainingService trainingService;

  public TrainingController(TrainingService trainingService) {
    this.trainingService = trainingService;
  }


  @PostMapping("/{employeeId}")
  public ResponseEntity<TrainingDTO> addTraining(
      @PathVariable Long employeeId,
      @RequestBody TrainingDTO dto
  ) {
    TrainingDTO created = trainingService.addTraining(employeeId, dto);
    return ResponseEntity.status(201).body(created);  // 201 Created
  }


  @GetMapping("/person/{personId}")
  public ResponseEntity<List<TrainingDTO>> getTrainingsByPerson(@PathVariable Long personId) {
    List<TrainingDTO> trainings = trainingService.getTrainingsByPerson(personId);
    return ResponseEntity.ok(trainings);
  }

  @GetMapping("/check/{personId}")
  public ResponseEntity<List<TrainingDTO>> checkExpiredTrainings(@PathVariable Long personId) {
    List<TrainingDTO> trainings = trainingService.checkExpiredTrainings(personId);
    return ResponseEntity.ok(trainings);
  }
}
