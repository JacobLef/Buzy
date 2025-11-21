package edu.neu.csye6200.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for Training data transfer.
 * Uses Builder pattern for construction.
 */
public class TrainingDTO {

    private final Long id;
    private final String trainingName;
    private final String description;
    private final LocalDate completionDate;
    private final LocalDate expiryDate;
    private final boolean required;
    private final boolean expired;
    private final Long personId;
    private final String personName;
    private final LocalDateTime createdAt;

    private TrainingDTO(Builder builder) {
        this.id = builder.id;
        this.trainingName = builder.trainingName;
        this.description = builder.description;
        this.completionDate = builder.completionDate;
        this.expiryDate = builder.expiryDate;
        this.required = builder.required;
        this.expired = builder.expired;
        this.personId = builder.personId;
        this.personName = builder.personName;
        this.createdAt = builder.createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getCompletionDate() {
        return completionDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isExpired() {
        return expired;
    }

    public Long getPersonId() {
        return personId;
    }

    public String getPersonName() {
        return personName;
    }

    public LocalDateTime getCreatedAt() {//read
        return createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder Class
     */
    public static class Builder {
        private Long id;
        private String trainingName;
        private String description;
        private LocalDate completionDate;
        private LocalDate expiryDate;
        private boolean required;
        private boolean expired;
        private Long personId;
        private String personName;
        private LocalDateTime createdAt;

        public Builder withId(Long id) {//write
            this.id = id;
            return this;
        }

        public Builder withTrainingName(String trainingName) {
            this.trainingName = trainingName;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withCompletionDate(LocalDate completionDate) {
            this.completionDate = completionDate;
            return this;
        }

        public Builder withExpiryDate(LocalDate expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public Builder withRequired(boolean required) {
            this.required = required;
            return this;
        }

        public Builder withExpired(boolean expired) {
            this.expired = expired;
            return this;
        }

        public Builder withPersonId(Long personId) {
            this.personId = personId;
            return this;
        }

        public Builder withPersonName(String personName) {
            this.personName = personName;
            return this;
        }

        public Builder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TrainingDTO build() {
            return new TrainingDTO(this);
        }
    }
}
