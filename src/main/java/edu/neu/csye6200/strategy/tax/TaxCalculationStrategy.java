package edu.neu.csye6200.strategy.tax;

/**
 * Strategy interface for calculating tax based on gross pay.
 * 
 * PayrollService uses this strategy to calculate tax deductions for employee paychecks.
 * Different implementations can be swapped at runtime (e.g., FlatTaxStrategy for simple
 * uniform rates, ProgressiveTaxStrategy for tiered tax brackets).
 * 
 * @author Qing Mi
 */
public interface TaxCalculationStrategy {
    double calculateTax(double grossPay);
    
    String getStrategyName();
}

