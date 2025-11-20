package com.stxvxn.app.dto.request;

import com.stxvxn.app.model.EmployeeRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para crear o actualizar un empleado.
 */
@Data
public class EmployeeRequest {
    
    @NotBlank(message = "El nombre es requerido")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String name;
    
    @NotBlank(message = "El email es requerido")
    @Email(message = "El formato del email no es válido")
    private String email;
    
    @NotNull(message = "El rol es requerido")
    private EmployeeRole role;
    
    @Size(max = 50, message = "El departamento no puede exceder 50 caracteres")
    private String department;
}

