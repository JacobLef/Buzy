package edu.neu.csye6200.model.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Training entity mapped to TRAINING table.
 *
 * ER 图字段：
 *  - id (PK)
 *  - training_name
 *  - description
 *  - completion_date
 *  - expiry_date
 *  - person_id (FK → BUSINESS_PERSON)
 *  - is_required
 *  - created_at
 *
 * UML 属性：
 *  - Long id
 *  - String trainingName
 *  - String description
 *  - Date completionDate
 *  - Date expiryDate
 *  - BusinessPerson person
 *  - Boolean isRequired
 *  + isExpired()
 *  + getRemainingDays()
 */
@Entity
@Table(name = "training")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "training_name", nullable = false)
    private String trainingName;

    @Column
    private String description;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private BusinessPerson person;

    @Column(name = "is_required", nullable = false)
    private boolean required;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 用于 Service 中临时标记过期状态的字段，不入库。
     * UML 需要 isExpired()，我们通过这个字段 + 日期计算提供。
     */
    @Transient
    private boolean expired;

    // -------------------- 构造函数 --------------------

    // JPA 默认构造函数
    public Training() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 与 TrainingServiceImpl 中 new Training(...) 完全匹配的构造函数：
     *
     * new Training(
     *    dto.getTrainingName(),
     *    dto.getDescription(),
     *    dto.getCompletionDate(),
     *    dto.getExpiryDate(),
     *    dto.isRequired()
     * )
     */
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

    // -------------------- Getter / Setter --------------------

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

    // 不提供 setCreatedAt，因为由生命周期方法维护

    // -------------------- UML 要求的方法 --------------------

    /**
     * 是否已经过期：
     * 1. 如果没有 expiryDate，则视为未过期
     * 2. 如果 expiryDate 在今天之前，则过期
     */
    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return expiryDate.isBefore(LocalDate.now());
    }

    /**
     * 供 Service 调用的 setter（虽然 expired 不入库，但可以保持 API 对称）
     */
    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    /**
     * 距离过期还剩多少天（UML: getRemainingDays()）
     * 如果没有 expiryDate，返回 0。
     */
    public long getRemainingDays() {
        if (expiryDate == null) {
            return 0L;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    @Override
    public String toString() {
        return "Training{" +
                "id=" + id +
                ", trainingName='" + trainingName + '\'' +
                ", description='" + description + '\'' +
                ", completionDate=" + completionDate +
                ", expiryDate=" + expiryDate +
                ", required=" + required +
                ", person=" + (person != null ? person.getName() : "null") +
                '}';
    }
}
