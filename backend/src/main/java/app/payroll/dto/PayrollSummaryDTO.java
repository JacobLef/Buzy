
package app.payroll.dto;

import java.time.LocalDate;

/**
 * DTO for Payroll Summary/Report response Contains aggregated statistics for
 * payroll analysis
 *
 * @author Qing Mi
 */
public record PayrollSummaryDTO(Long businessId, String businessName, LocalDate startDate, LocalDate endDate,
		Integer totalPaychecks, Integer totalEmployees, Double totalGrossPay, Double totalBonus,
		Double totalTaxDeductions, Double totalInsuranceDeductions, Double totalDeductions, Double totalNetPay,
		Double averageGrossPay, Double averageNetPay, Double averageTaxRate, Double averageInsuranceRate) {
	public PayrollSummaryDTO {
		// Calculate averages if counts are valid
		if (totalPaychecks != null && totalPaychecks > 0) {
			averageGrossPay = totalGrossPay != null ? totalGrossPay / totalPaychecks : 0.0;
			averageNetPay = totalNetPay != null ? totalNetPay / totalPaychecks : 0.0;
		} else {
			averageGrossPay = 0.0;
			averageNetPay = 0.0;
		}

		// Calculate average rates
		// Denominator should be totalGrossPay + totalBonus because deductions are
		// calculated on total compensation
		double totalCompensation = (totalGrossPay != null ? totalGrossPay : 0.0)
				+ (totalBonus != null ? totalBonus : 0.0);
		if (totalCompensation > 0) {
			averageTaxRate = totalTaxDeductions != null ? totalTaxDeductions / totalCompensation : 0.0;
			averageInsuranceRate = totalInsuranceDeductions != null
					? totalInsuranceDeductions / totalCompensation
					: 0.0;
		} else {
			averageTaxRate = 0.0;
			averageInsuranceRate = 0.0;
		}
	}
}
