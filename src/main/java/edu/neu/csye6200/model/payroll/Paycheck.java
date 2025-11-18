package edu.neu.csye6200.model.payroll;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Paycheck entity representing a paycheck record for an employee.
 */
@Entity
@Table(name = "paycheck")
public class Paycheck {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;
    
    // TODO: Replace employeeId with Employee entity relation when Employee class is implemented
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "employee_id", nullable = false)
    // private Employee employee;
    
    @Column(name = "gross_pay", nullable = false)
    private double grossPay;
    
    @Column(name = "tax_deduction", nullable = false)
    private double taxDeduction;
    
    @Column(name = "insurance_deduction", nullable = false)
    private double insuranceDeduction;
    
    @Column(name = "bonus")
    private Double bonus;
    
    @Column(name = "net_pay", nullable = false)
    private double netPay;
    
    @Column(name = "pay_date", nullable = false)
    private LocalDate payDate;
    
    @Column(name = "pay_period")
    private String payPeriod;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public Paycheck() {
    }
    
    public Paycheck(Long employeeId, double grossPay, double taxDeduction, 
                   double insuranceDeduction, LocalDate payDate) {
        this.employeeId = employeeId;
        this.grossPay = grossPay;
        this.taxDeduction = taxDeduction;
        this.insuranceDeduction = insuranceDeduction;
        this.payDate = payDate;
        this.createdAt = LocalDateTime.now();
        this.netPay = calculateNetPay();
    }
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (netPay == 0.0 && grossPay > 0) {
            netPay = calculateNetPay();
        }
    }
    
    public double calculateNetPay() {
        double bonusAmount = (bonus != null) ? bonus : 0.0;
        return grossPay - taxDeduction - insuranceDeduction + bonusAmount;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
    
    public double getGrossPay() {
        return grossPay;
    }
    
    public void setGrossPay(double grossPay) {
        this.grossPay = grossPay;
        this.netPay = calculateNetPay();
    }
    
    public double getTaxDeduction() {
        return taxDeduction;
    }
    
    public void setTaxDeduction(double taxDeduction) {
        this.taxDeduction = taxDeduction;
        this.netPay = calculateNetPay();
    }
    
    public double getInsuranceDeduction() {
        return insuranceDeduction;
    }
    
    public void setInsuranceDeduction(double insuranceDeduction) {
        this.insuranceDeduction = insuranceDeduction;
        this.netPay = calculateNetPay();
    }
    
    public Double getBonus() {
        return bonus;
    }
    
    public void setBonus(Double bonus) {
        this.bonus = bonus;
        this.netPay = calculateNetPay();
    }
    
    public double getNetPay() {
        return netPay;
    }
    
    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }
    
    public LocalDate getPayDate() {
        return payDate;
    }
    
    public void setPayDate(LocalDate payDate) {
        this.payDate = payDate;
    }
    
    public String getPayPeriod() {
        return payPeriod;
    }
    
    public void setPayPeriod(String payPeriod) {
        this.payPeriod = payPeriod;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

