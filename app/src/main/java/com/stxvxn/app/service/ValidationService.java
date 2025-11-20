package com.stxvxn.app.service;

import com.stxvxn.app.dto.CreatePackageRequest;
import com.stxvxn.app.model.PackageStatus;

/**
 * Servicio para validaciones de negocio centralizadas.
 * Separa la lógica de validación de los servicios principales.
 */
public interface ValidationService {
    
    /**
     * Valida los datos de un paquete antes de crearlo.
     * 
     * @param request Request con los datos del paquete
     * @throws com.stxvxn.app.exception.ValidationException Si la validación falla
     */
    void validatePackageData(CreatePackageRequest request);
    
    /**
     * Valida que una transición de estado sea válida.
     * 
     * @param currentStatus Estado actual
     * @param newStatus Estado nuevo
     * @throws com.stxvxn.app.exception.InvalidStatusTransitionException Si la transición es inválida
     */
    void validateStatusTransition(PackageStatus currentStatus, PackageStatus newStatus);
    
    /**
     * Valida que un número de teléfono tenga formato válido.
     * 
     * @param phone Número de teléfono
     * @return true si es válido
     */
    boolean isValidPhoneNumber(String phone);
    
    /**
     * Valida que un email tenga formato válido.
     * 
     * @param email Email a validar
     * @return true si es válido
     */
    boolean isValidEmail(String email);
}

