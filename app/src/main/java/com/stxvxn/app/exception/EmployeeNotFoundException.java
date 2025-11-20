package com.stxvxn.app.exception;

/**
 * Excepción lanzada cuando un empleado no se encuentra en el sistema.
 */
public class EmployeeNotFoundException extends BusinessException {
    
    public EmployeeNotFoundException(String employeeId) {
        super(
            "Empleado no encontrado con ID: " + employeeId,
            "EMPLOYEE_NOT_FOUND"
        );
    }
}

