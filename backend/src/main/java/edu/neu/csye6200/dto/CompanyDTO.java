package edu.neu.csye6200.dto;

import java.util.List;

/**
 * Data Transfer Object for Company entity
 * Used for API responses
 * 
 * @author Qing Mi
 */
public class CompanyDTO extends BusinessDTO {
    
    private final int totalEmployees;
    private final int totalEmployers;
    private final int totalPersons;
    private final List<Long> employeeIds;
    private final List<Long> employerIds;

    private CompanyDTO(Builder builder) {
        super(builder);
        this.totalEmployees = builder.totalEmployees;
        this.totalEmployers = builder.totalEmployers;
        this.totalPersons = builder.totalPersons;
        this.employeeIds = builder.employeeIds;
        this.employerIds = builder.employerIds;
    }

    @Override
    public String getBusinessType() {
        return "Company";
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public int getTotalEmployers() {
        return totalEmployers;
    }

    public int getTotalPersons() {
        return totalPersons;
    }

    public List<Long> getEmployeeIds() {
        return employeeIds;
    }

    public List<Long> getEmployerIds() {
        return employerIds;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends BusinessDTO.Builder<Builder> {
    private int totalEmployees;
    private int totalEmployers;
    private int totalPersons;
    private List<Long> employeeIds;
    private List<Long> employerIds;

        @Override
        protected Builder self() {
            return this;
        }

        public Builder withTotalEmployees(int totalEmployees) {
            this.totalEmployees = totalEmployees;
            return this;
        }

        public Builder withTotalEmployers(int totalEmployers) {
            this.totalEmployers = totalEmployers;
            return this;
        }

        public Builder withTotalPersons(int totalPersons) {
            this.totalPersons = totalPersons;
            return this;
        }

        public Builder withEmployeeIds(List<Long> employeeIds) {
            this.employeeIds = employeeIds;
            return this;
        }

        public Builder withEmployerIds(List<Long> employerIds) {
            this.employerIds = employerIds;
            return this;
        }

        @Override
        public CompanyDTO build() {
            return new CompanyDTO(this);
        }
    }
}
    