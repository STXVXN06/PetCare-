package com.stxvxn.app.util;

import com.stxvxn.app.repository.PackageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Generador de números de rastreo únicos.
 * Genera números en formato TRK-XXXXXXXX (8 caracteres alfanuméricos).
 */
@Component
@Slf4j
public class TrackingNumberGenerator {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();
    
    /**
     * Genera un número de rastreo aleatorio.
     * 
     * @return Número de rastreo generado
     */
    public String generate() {
        StringBuilder sb = new StringBuilder(Constants.TRACKING_PREFIX);
        
        for (int i = 0; i < Constants.TRACKING_NUMBER_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        
        return sb.toString();
    }
    
    /**
     * Genera un número de rastreo único verificando en la base de datos.
     * 
     * @param repository Repositorio para verificar existencia
     * @return Número de rastreo único
     * @throws IllegalStateException Si no se puede generar un número único después de 100 intentos
     */
    public String generateUnique(PackageRepository repository) {
        String trackingNumber;
        int attempts = 0;
        int maxAttempts = 100;
        
        do {
            trackingNumber = generate();
            attempts++;
            
            if (attempts >= maxAttempts) {
                log.error("No se pudo generar un número de rastreo único después de {} intentos", maxAttempts);
                throw new IllegalStateException(
                    "No se pudo generar un número de rastreo único. Por favor, intente nuevamente."
                );
            }
        } while (repository.findByTrackingNumber(trackingNumber).isPresent());
        
        log.debug("Tracking number generated after {} attempts: {}", attempts, trackingNumber);
        return trackingNumber;
    }
}

