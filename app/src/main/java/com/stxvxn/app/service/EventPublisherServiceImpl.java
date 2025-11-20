package com.stxvxn.app.service;

import com.stxvxn.app.event.PackageCreatedEvent;
import com.stxvxn.app.event.PackageStatusChangedEvent;
import com.stxvxn.app.model.Package;
import com.stxvxn.app.model.PackageStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de publicación de eventos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisherServiceImpl implements EventPublisherService {
    
    private final ApplicationEventPublisher eventPublisher;
    
    @Override
    public void publishPackageCreatedEvent(Package packageEntity) {
        log.info("Publishing PackageCreatedEvent for tracking: {}", 
                packageEntity.getTrackingNumber());
        
        PackageCreatedEvent event = new PackageCreatedEvent(this, packageEntity);
        eventPublisher.publishEvent(event);
        
        log.debug("PackageCreatedEvent published successfully");
    }
    
    @Override
    public void publishStatusChangedEvent(
            Package packageEntity, 
            PackageStatus oldStatus, 
            String employeeId) {
        log.info("Publishing StatusChangedEvent for tracking: {} ({} -> {})", 
                packageEntity.getTrackingNumber(), oldStatus, packageEntity.getStatus());
        
        PackageStatusChangedEvent event = new PackageStatusChangedEvent(
            this,
            packageEntity.getId(),
            packageEntity.getTrackingNumber(),
            oldStatus,
            packageEntity.getStatus(),
            employeeId
        );
        eventPublisher.publishEvent(event);
        
        log.debug("StatusChangedEvent published successfully");
    }
}

