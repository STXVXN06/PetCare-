package com.stxvxn.app.service;

import com.stxvxn.app.dto.CreatePackageRequest;
import com.stxvxn.app.exception.InvalidStatusTransitionException;
import com.stxvxn.app.exception.ValidationException;
import com.stxvxn.app.model.PackageStatus;
import com.stxvxn.app.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Implementación del servicio de validaciones.
 */
@Service
@Slf4j
public class ValidationServiceImpl implements ValidationService {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    @Override
    public void validatePackageData(CreatePackageRequest request) {
        if (request == null) {
            throw new ValidationException("El request no puede ser nulo");
        }
        
        if (request.getRecipientName() == null || request.getRecipientName().trim().isEmpty()) {
            throw new ValidationException("El nombre del destinatario es requerido");
        }
        
        if (request.getRecipientPhone() == null || request.getRecipientPhone().trim().isEmpty()) {
            throw new ValidationException("El teléfono del destinatario es requerido");
        }
        
        if (!isValidPhoneNumber(request.getRecipientPhone())) {
            throw new ValidationException(
                "El formato del teléfono no es válido. Debe tener entre 10 y 15 dígitos."
            );
        }
        
        log.debug("Package data validation passed for recipient: {}", request.getRecipientName());
    }
    
    @Override
    public void validateStatusTransition(PackageStatus current, PackageStatus newStatus) {
        if (current == null || newStatus == null) {
            throw new ValidationException("Los estados no pueden ser nulos");
        }
        
        if (Constants.FINAL_STATUSES.contains(current)) {
            throw new InvalidStatusTransitionException(
                current, 
                newStatus,
                "No se puede cambiar el estado de un paquete en estado final: " + current
            );
        }
        
        if (!current.canTransitionTo(newStatus)) {
            throw new InvalidStatusTransitionException(
                current,
                newStatus,
                String.format("Transición inválida de %s a %s", current, newStatus)
            );
        }
        
        log.debug("Valid status transition: {} -> {}", current, newStatus);
    }
    
    @Override
    public boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    @Override
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
}

