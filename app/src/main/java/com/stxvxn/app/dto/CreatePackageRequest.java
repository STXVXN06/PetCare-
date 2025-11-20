package com.stxvxn.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para crear un nuevo paquete.
 */
@Data
public class CreatePackageRequest {
    
    @NotBlank(message = "El nombre del destinatario es requerido")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String recipientName;
    
    @NotBlank(message = "La dirección del destinatario es requerida")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String recipientAddress;
    
    @NotBlank(message = "El teléfono del destinatario es requerido")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Formato de teléfono inválido")
    private String recipientPhone;
    
    @NotBlank(message = "El nombre del remitente es requerido")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String senderName;
    
    @NotBlank(message = "La dirección del remitente es requerida")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String senderAddress;
}

