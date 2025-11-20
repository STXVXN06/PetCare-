package com.stxvxn.app.event;

import com.stxvxn.app.model.Package;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Evento publicado cuando se crea un nuevo paquete.
 */
@Getter
public class PackageCreatedEvent extends ApplicationEvent {
    private final Package packageEntity;
    private final LocalDateTime eventTimestamp;
    
    public PackageCreatedEvent(Object source, Package packageEntity) {
        super(source);
        this.packageEntity = packageEntity;
        this.eventTimestamp = LocalDateTime.now();
    }
}

