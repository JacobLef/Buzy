package app.training;

import app.training.dto.TrainingDTO;
import app.training.dto.UpdateTrainingRequest;
import app.training.dto.CreateTrainingRequest;

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

	@PostMapping("/person/{personId}")
	public ResponseEntity<TrainingDTO> addTraining(@PathVariable Long personId,
			@RequestBody CreateTrainingRequest request) {
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
	public ResponseEntity<TrainingDTO> updateTraining(@PathVariable Long trainingId,
			@RequestBody UpdateTrainingRequest request) {
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
