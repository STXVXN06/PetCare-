package com.stxvxn.app.dto;

import com.stxvxn.app.model.PackageStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para actualizar el estado de un paquete.
 */
@Data
public class UpdateStatusRequest {
    
    @NotNull(message = "El estado es requerido")
    private PackageStatus status;
    
    @NotBlank(message = "El ID del empleado es requerido")
    private String updatedBy;
    
    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notes;
    
    @Size(max = 200, message = "La ubicación no puede exceder 200 caracteres")
    private String location;
}

