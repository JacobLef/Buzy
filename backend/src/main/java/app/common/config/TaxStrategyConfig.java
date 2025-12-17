package app.common.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import app.payroll.strategy.FlatTaxStrategy;
import app.payroll.strategy.ProgressiveTaxStrategy;
import app.payroll.strategy.TaxCalculationStrategy;

/**
 * Configuration for Tax Calculation Strategy beans. Provides default FlatTaxStrategy and optional
 * ProgressiveTaxStrategy.
 */
@Configuration
public class TaxStrategyConfig {

  @Value("${payroll.default.tax.rate:0.20}")
  private double defaultTaxRate;

  /**
   * Default flat tax strategy bean. Used by PayrollServiceImpl as the primary tax calculation
   * strategy.
   */
  @Bean(name = "flatTaxStrategy")
  @Primary
  public TaxCalculationStrategy flatTaxStrategy() {
    return new FlatTaxStrategy(defaultTaxRate);
  }

  /** Progressive tax strategy bean (optional). Can be used for tiered tax calculations. */
  @Bean(name = "progressiveTaxStrategy")
  public TaxCalculationStrategy progressiveTaxStrategy() {
    // Default progressive tax brackets
    Map<Double, Double> brackets = new HashMap<>();
    brackets.put(10000.0, 0.10); // First $10,000 at 10%
    brackets.put(50000.0, 0.20); // $10,001-$50,000 at 20%
    brackets.put(100000.0, 0.30); // $50,001-$100,000 at 30%
    // Above $100,000 uses highest rate (30%)

    return new ProgressiveTaxStrategy(brackets);
  }
}
