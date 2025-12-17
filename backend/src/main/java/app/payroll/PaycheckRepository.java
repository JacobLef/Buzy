package app.payroll;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Paycheck entity
 *
 * @author Qing Mi
 */
@Repository
public interface PaycheckRepository extends JpaRepository<Paycheck, Long> {

	/**
	 * Find all paychecks for a specific employee
	 */
	@Query("SELECT p FROM Paycheck p WHERE p.employee.id = :employeeId")
	List<Paycheck> findByEmployeeId(Long employeeId);

	/**
	 * Find paychecks within a date range
	 */
	List<Paycheck> findByPayDateBetween(LocalDate startDate, LocalDate endDate);

	/**
	 * Find paychecks for a specific employee within a date range
	 */
	@Query("SELECT p FROM Paycheck p WHERE p.employee.id = :employeeId "
			+ "AND p.payDate BETWEEN :startDate AND :endDate")
	List<Paycheck> findByEmployeeIdAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate);

	/**
	 * Find paychecks for employees in a business within a date range
	 */
	@Query("SELECT p FROM Paycheck p WHERE p.employee.company.id = :businessId "
			+ "AND p.payDate BETWEEN :startDate AND :endDate")
	List<Paycheck> findByBusinessIdAndDateRange(Long businessId, LocalDate startDate, LocalDate endDate);

	/**
	 * Find paychecks for employees in a business after a specific date
	 */
	@Query("SELECT p FROM Paycheck p WHERE p.employee.company.id = :businessId " + "AND p.payDate >= :date")
	List<Paycheck> findByBusinessIdAndPayDateAfter(Long businessId, LocalDate date);
}
