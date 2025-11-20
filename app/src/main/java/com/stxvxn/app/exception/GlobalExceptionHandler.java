package com.stxvxn.app.exception;

import com.stxvxn.app.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejo global de excepciones para toda la aplicación.
 * Centraliza el manejo de errores y proporciona respuestas consistentes.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(PackageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePackageNotFound(
            PackageNotFoundException ex, 
            HttpServletRequest request) {
        log.warn("Package not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .error("Paquete no encontrado")
            .message(ex.getMessage())
            .code(ex.getErrorCode())
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransition(
            InvalidStatusTransitionException ex,
            HttpServletRequest request) {
        log.warn("Invalid status transition: {} -> {}", 
                ex.getCurrentStatus(), ex.getAttemptedStatus());
        
        Map<String, Object> details = new HashMap<>();
        details.put("currentStatus", ex.getCurrentStatus());
        details.put("attemptedStatus", ex.getAttemptedStatus());
        
        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .error("Transición de estado inválida")
            .message(ex.getMessage())
            .code(ex.getErrorCode())
            .details(details)
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeNotFound(
            EmployeeNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Employee not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .error("Empleado no encontrado")
            .message(ex.getMessage())
            .code(ex.getErrorCode())
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex,
            HttpServletRequest request) {
        log.warn("Validation error: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .error("Error de validación")
            .message(ex.getMessage())
            .code(ex.getErrorCode())
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        log.warn("Bean validation errors: {}", ex.getBindingResult().getErrorCount());
        
        Map<String, Object> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            details.put(error.getField(), error.getDefaultMessage());
        });
        
        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .error("Error de validación")
            .message("Los datos proporcionados no son válidos")
            .code("VALIDATION_ERROR")
            .details(details)
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        log.warn("Illegal argument: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .error("Argumento inválido")
            .message(ex.getMessage())
            .code("ILLEGAL_ARGUMENT")
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error in path: {}", request.getRequestURI(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
            .success(false)
            .error("Error interno del servidor")
            .message("Ocurrió un error inesperado. Por favor, intente más tarde.")
            .code("INTERNAL_ERROR")
            .path(request.getRequestURI())
            .timestamp(LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

