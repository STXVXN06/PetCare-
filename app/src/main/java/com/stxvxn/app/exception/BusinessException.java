package com.stxvxn.app.exception;

import java.time.LocalDateTime;

/**
 * Excepción base para errores de negocio.
 * Todas las excepciones de negocio deben extender esta clase.
 */
public class BusinessException extends RuntimeException {
    private final String errorCode;
    private final LocalDateTime timestamp;
    
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

