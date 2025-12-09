package edu.neu.csye6200.dto.response;

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

