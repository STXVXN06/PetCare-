package com.stxvxn.app.service;

import com.stxvxn.app.model.Package;
import com.stxvxn.app.model.PackageStatus;

/**
 * Servicio para publicar eventos de dominio.
 * Centraliza la publicación de eventos en la aplicación.
 */
public interface EventPublisherService {
    
    /**
     * Publica un evento cuando se crea un paquete.
     * 
     * @param packageEntity Paquete creado
     */
    void publishPackageCreatedEvent(Package packageEntity);
    
    /**
     * Publica un evento cuando cambia el estado de un paquete.
     * 
     * @param packageEntity Paquete actualizado
     * @param oldStatus Estado anterior
     * @param employeeId ID del empleado que realizó el cambio
     */
    void publishStatusChangedEvent(Package packageEntity, PackageStatus oldStatus, String employeeId);
}

