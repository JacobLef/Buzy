package app.business.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Abstract DTO base class for Business data transfer. Contains common fields
 * shared by business entities like Company. Uses Builder pattern for object
 * construction.
 */
public abstract class BusinessDTO {
	private final Long id;
	private final String name;
	private final String address;
	private final String industry;
	private final LocalDate foundedDate;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	protected BusinessDTO(Builder<?> builder) {
		this.id = builder.id;
		this.name = builder.name;
		this.address = builder.address;
		this.industry = builder.industry;
		this.foundedDate = builder.foundedDate;
		this.createdAt = builder.createdAt;
		this.updatedAt = builder.updatedAt;
	}

	/**
	 * Get the type of business (e.g., "Company").
	 */
	public abstract String getBusinessType();

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getIndustry() {
		return industry;
	}

	public LocalDate getFoundedDate() {
		return foundedDate;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * Abstract builder class for BusinessDTO.
	 */
	public abstract static class Builder<T extends Builder<T>> {
		private Long id;
		private String name;
		private String address;
		private String industry;
		private LocalDate foundedDate;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;

		/**
		 * Return this builder.
		 *
		 * @return the type of this Builder.
		 */
		protected abstract T self();

		public T withId(Long id) {
			this.id = id;
			return self();
		}

		public T withName(String name) {
			this.name = name;
			return self();
		}

		public T withAddress(String address) {
			this.address = address;
			return self();
		}

		public T withIndustry(String industry) {
			this.industry = industry;
			return self();
		}

		public T withFoundedDate(LocalDate foundedDate) {
			this.foundedDate = foundedDate;
			return self();
		}

		public T createdAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
			return self();
		}

		public T updatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
			return self();
		}

		public abstract BusinessDTO build();
	}
}
