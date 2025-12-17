package app.payroll.dto;

/**
 * DTO for tax strategy switch response Can contain either success information
 * (message, strategy) or error information
 *
 * @author Qing Mi
 */
public record TaxStrategySwitchResponse(String message, String strategy, String error) {
	/**
	 * Create a success response
	 */
	public static TaxStrategySwitchResponse success(String message, String strategy) {
		return new TaxStrategySwitchResponse(message, strategy, null);
	}

	/**
	 * Create an error response
	 */
	public static TaxStrategySwitchResponse error(String errorMessage) {
		return new TaxStrategySwitchResponse(null, null, errorMessage);
	}
}
