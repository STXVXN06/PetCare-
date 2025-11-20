package com.stxvxn.app.event.listener;

import com.stxvxn.app.event.PackageCreatedEvent;
import com.stxvxn.app.event.PackageStatusChangedEvent;
import com.stxvxn.app.repository.PackageRepository;
import com.stxvxn.app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener para eventos relacionados con paquetes.
 * Procesa eventos de forma asíncrona para no bloquear el hilo principal.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PackageEventListener {
    
    private final NotificationService notificationService;
    private final PackageRepository packageRepository;
    
    /**
     * Maneja el evento de creación de paquete.
     * Crea la notificación inicial de forma asíncrona.
     * 
     * @param event Evento de creación de paquete
     */
    @EventListener
    @Async
    public void handlePackageCreated(PackageCreatedEvent event) {
        log.info("Handling PackageCreatedEvent for tracking: {}", 
                event.getPackageEntity().getTrackingNumber());
        
        try {
            var pkg = event.getPackageEntity();
            
            // Crear notificación inicial
            notificationService.createStatusUpdateNotification(
                pkg.getId(),
                pkg.getTrackingNumber(),
                pkg.getRecipientPhone(),
                pkg.getStatus()
            );
            
            log.debug("Initial notification created for package: {}", pkg.getTrackingNumber());
            
        } catch (Exception e) {
            log.error("Error handling PackageCreatedEvent for tracking: {}", 
                     event.getPackageEntity().getTrackingNumber(), e);
            // No relanzar la excepción para no afectar el flujo principal
        }
    }
    
    /**
     * Maneja el evento de cambio de estado.
     * Crea notificación de cambio de estado de forma asíncrona.
     * 
     * @param event Evento de cambio de estado
     */
    @EventListener
    @Async
    public void handleStatusChanged(PackageStatusChangedEvent event) {
        log.info("Handling StatusChangedEvent for tracking: {} ({} -> {})", 
                event.getTrackingNumber(), event.getOldStatus(), event.getNewStatus());
        
        try {
            // Obtener el paquete completo para crear la notificación
            var pkgOpt = packageRepository.findByTrackingNumber(event.getTrackingNumber());
            
            if (pkgOpt.isEmpty()) {
                log.warn("Package not found for StatusChangedEvent: {}", event.getTrackingNumber());
                return;
            }
            
            var pkg = pkgOpt.get();
            
            // Crear notificación de cambio de estado
            notificationService.createStatusUpdateNotification(
                pkg.getId(),
                pkg.getTrackingNumber(),
                pkg.getRecipientPhone(),
                event.getNewStatus()
            );
            
            log.debug("Status change notification created for package: {}", event.getTrackingNumber());
            
        } catch (Exception e) {
            log.error("Error handling StatusChangedEvent for tracking: {}", 
                     event.getTrackingNumber(), e);
            // No relanzar la excepción para no afectar el flujo principal
        }
    }
}

