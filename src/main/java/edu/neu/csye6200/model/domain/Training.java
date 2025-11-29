package edu.neu.csye6200.model.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Training entity representing a training record for business persons.
 * Can be assigned to either an Employee or Employer (both extend BusinessPerson).
 */
@Entity
@Table(name = "training")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "training_name", nullable = false)
    private String trainingName;

    @Column(length = 500)
    private String description;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private BusinessPerson person;

    @Column(name = "is_required", nullable = false)
    private boolean required;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // -------------------- Constructors --------------------

    public Training() {
        this.createdAt = LocalDateTime.now();
    }

    public Training(String trainingName,
                    String description,
                    LocalDate completionDate,
                    LocalDate expiryDate,
                    boolean required) {
        this.trainingName = trainingName;
        this.description = description;
        this.completionDate = completionDate;
        this.expiryDate = expiryDate;
        this.required = required;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // -------------------- Business Logic --------------------

    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return expiryDate.isBefore(LocalDate.now());
    }

    public long getRemainingDays() {
        if (expiryDate == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    // -------------------- Getters / Setters --------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate) {
        this.completionDate = completionDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BusinessPerson getPerson() {
        return person;
    }

    public void setPerson(BusinessPerson person) {
        this.person = person;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Training{" +
            "id=" + id +
            ", trainingName='" + trainingName + '\'' +
            ", personType=" + (person != null ? person.getPersonType() : "null") +
            ", personName=" + (person != null ? person.getName() : "null") +
            ", required=" + required +
            ", expired=" + isExpired() +
            '}';
    }
}