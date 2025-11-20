package com.stxvxn.app.exception;

/**
 * Excepción lanzada cuando falla una validación de negocio.
 */
public class ValidationException extends BusinessException {
    
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
}

