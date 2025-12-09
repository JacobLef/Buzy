package app.payroll.strategy;

import java.util.Map;
import java.util.TreeMap;

/**
 * Progressive tax strategy that applies different tax rates to different income brackets.
 * Each bracket is taxed at its specific rate, with higher income brackets taxed at higher rates.
 * 
 * Example use case:
 * A company implements a tiered tax system:
 * - First $10,000: 10% tax rate
 * - $10,001 to $50,000: 20% tax rate  
 * - Above $50,000: 30% tax rate
 * 
 * For an employee with $60,000 gross pay:
 * - $0     -$10,000: $10,000 * 0.10 = $1,000
 * - $10,001-$50,000: $40,000 * 0.20 = $8,000
 * - $50,001-$60,000: $10,000 * 0.30 = $3,000
 * Total tax = $12,000
 * 
 * Usage:
 * Map<Double, Double> brackets = new HashMap<>();
 * brackets.put(10000.0, 0.10);
 * brackets.put(50000.0, 0.20);
 * TaxCalculationStrategy strategy = new ProgressiveTaxStrategy(brackets);
 * double tax = strategy.calculateTax(60000.0); // Returns 12000.0
 * 
 * @author Qing Mi
 */
public class ProgressiveTaxStrategy implements TaxCalculationStrategy {
    // TreeMap: auto-sorts by threshold (ascending), ensures correct bracket order
    private final Map<Double, Double> taxBrackets; 
    
    public ProgressiveTaxStrategy(Map<Double, Double> taxBrackets) {
        if (taxBrackets == null || taxBrackets.isEmpty()) {
            throw new IllegalArgumentException("Tax brackets cannot be null or empty");
        }

        this.taxBrackets = new TreeMap<>(taxBrackets); 
        validateBrackets();
    }
    
    private void validateBrackets() {
        for (Map.Entry<Double, Double> entry : taxBrackets.entrySet()) {
            if (entry.getKey() < 0) {
                throw new IllegalArgumentException("Tax bracket threshold cannot be negative");
            }
            if (entry.getValue() < 0 || entry.getValue() > 1) {
                throw new IllegalArgumentException("Tax rate must be between 0 and 1");
            }
        }
    }
    
    @Override
    public double calculateTax(double grossPay) {
        if (grossPay < 0) {
            throw new IllegalArgumentException("Gross pay cannot be negative");
        }
        
        if (grossPay == 0) {
            return 0.0;
        }
        
        double totalTax = 0.0;
        double remainingIncome = grossPay;
        double previousThreshold = 0.0;
        
        // TreeMap iteration: guaranteed ascending order by threshold
        for (Map.Entry<Double, Double> bracket : taxBrackets.entrySet()) { 
            double threshold = bracket.getKey();
            double rate = bracket.getValue();
            
            if (remainingIncome <= 0) {
                break;
            }
            
            double taxableInBracket = Math.min(remainingIncome, threshold - previousThreshold);
            if (taxableInBracket > 0) {
                totalTax += taxableInBracket * rate;
                remainingIncome -= taxableInBracket;
            }
            
            previousThreshold = threshold;
        }
        
        if (remainingIncome > 0) {
            Double highestRate = taxBrackets.values().stream()
                    .max(Double::compareTo)
                    .orElse(0.0);
            totalTax += remainingIncome * highestRate;
        }
        
        return totalTax;
    }
    
    // Returns defensive copy to prevent external modification
    public Map<Double, Double> getTaxBrackets() {
        return new TreeMap<>(taxBrackets); 
    }
    
    @Override
    public String getStrategyName() {
        return "Progressive Tax Strategy";
    }
}

