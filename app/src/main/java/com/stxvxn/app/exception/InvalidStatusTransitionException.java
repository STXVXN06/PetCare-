package com.stxvxn.app.exception;

import com.stxvxn.app.model.PackageStatus;

/**
 * Excepción lanzada cuando se intenta realizar una transición de estado inválida.
 */
public class InvalidStatusTransitionException extends BusinessException {
    private final PackageStatus currentStatus;
    private final PackageStatus attemptedStatus;
    
    public InvalidStatusTransitionException(
            PackageStatus current, 
            PackageStatus attempted, 
            String message) {
        super(message, "INVALID_STATUS_TRANSITION");
        this.currentStatus = current;
        this.attemptedStatus = attempted;
    }
    
    public PackageStatus getCurrentStatus() {
        return currentStatus;
    }
    
    public PackageStatus getAttemptedStatus() {
        return attemptedStatus;
    }
}

