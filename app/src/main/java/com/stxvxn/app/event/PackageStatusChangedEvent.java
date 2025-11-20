package com.stxvxn.app.event;

import com.stxvxn.app.model.PackageStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * Evento publicado cuando cambia el estado de un paquete.
 */
@Getter
public class PackageStatusChangedEvent extends ApplicationEvent {
    private final String packageId;
    private final String trackingNumber;
    private final PackageStatus oldStatus;
    private final PackageStatus newStatus;
    private final String employeeId;
    private final LocalDateTime eventTimestamp;
    
    public PackageStatusChangedEvent(
            Object source,
            String packageId,
            String trackingNumber,
            PackageStatus oldStatus,
            PackageStatus newStatus,
            String employeeId) {
        super(source);
        this.packageId = packageId;
        this.trackingNumber = trackingNumber;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.employeeId = employeeId;
        this.eventTimestamp = LocalDateTime.now();
    }
}

