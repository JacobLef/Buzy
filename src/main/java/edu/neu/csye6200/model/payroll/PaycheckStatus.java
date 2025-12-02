package edu.neu.csye6200.model.payroll;

/**
 * Status enum for Paycheck lifecycle
 * 
 * @author Qing Mi
 */
public enum PaycheckStatus {
    DRAFT,      // Fully editable, can be deleted
    PENDING,    // Optional: if manager approval step is needed
    PAID,       // Locked, cannot be deleted (for audit reasons)
    VOIDED      // Cancelled/voided paycheck (creates negative entry)
}

