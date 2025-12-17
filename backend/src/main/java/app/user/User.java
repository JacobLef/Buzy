package app.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import app.business.BusinessPerson;

/**
 * User entity representing a login account. Email for authentication. User has
 * a foreign key to BusinessPerson (Employee or Employer).
 */
@Entity
@Table(name = "user", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Email for login - must match BusinessPerson.email. This is the unique
	 * identifier for authentication.
	 */
	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password; // BCrypt hashed password

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;

	@OneToOne
	@JoinColumn(name = "business_person_id")
	private BusinessPerson businessPerson;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(nullable = false)
	private Boolean enabled = true;

	public User() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	public User(String email, String password, UserRole role) {
		this();
		this.email = email;
		this.password = password;
		this.role = role;
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
		this.updatedAt = LocalDateTime.now();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		this.updatedAt = LocalDateTime.now();
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
		this.updatedAt = LocalDateTime.now();
	}

	public BusinessPerson getBusinessPerson() {
		return businessPerson;
	}

	public void setBusinessPerson(BusinessPerson businessPerson) {
		this.businessPerson = businessPerson;
		// Sync email with BusinessPerson email
		if (businessPerson != null && businessPerson.getEmail() != null) {
			this.email = businessPerson.getEmail();
		}
		this.updatedAt = LocalDateTime.now();
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
		this.updatedAt = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return "User{" + "id=" + id + ", email='" + email + '\'' + ", role=" + role + ", enabled=" + enabled + '}';
	}
}
