package edu.neu.csye6200.strategy.tax;

/**
 * Flat tax strategy that applies a uniform tax rate to all income.
 * 
 * Example use case:
 * A small business uses a flat 15% tax rate for all employees regardless of income level.
 * For an employee with $50,000 gross pay: tax = $50,000 * 0.15 = $7,500
 * 
 * Usage:
 * TaxCalculationStrategy strategy = new FlatTaxStrategy(0.15);
 * double tax = strategy.calculateTax(50000.0); // Returns 7500.0
 * 
 * @author Qing Mi
 */
public class FlatTaxStrategy implements TaxCalculationStrategy {
    private final double taxRate;
    
    public FlatTaxStrategy(double taxRate) {
        if (taxRate < 0 || taxRate > 1) {
            throw new IllegalArgumentException("Tax rate must be between 0 and 1");
        }
        this.taxRate = taxRate;
    }
    
    @Override
    public double calculateTax(double grossPay) {
        if (grossPay < 0) {
            throw new IllegalArgumentException("Gross pay cannot be negative");
        }
        return grossPay * taxRate;
    }
    
    public double getTaxRate() {
        return taxRate;
    }
    
    @Override
    public String getStrategyName() {
        return "Flat Tax Strategy";
    }
}

