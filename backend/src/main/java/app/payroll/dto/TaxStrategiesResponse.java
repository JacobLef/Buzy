package app.payroll.dto;

import java.util.Map;

/**
 * DTO for available tax strategies response
 * 
 * @author Qing Mi
 */
public record TaxStrategiesResponse(
    Map<String, String> strategies
) {
}

