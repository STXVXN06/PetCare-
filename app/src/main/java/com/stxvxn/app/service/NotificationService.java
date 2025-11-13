package com.stxvxn.app.service;

import com.stxvxn.app.model.Notification;
import com.stxvxn.app.model.PackageStatus;

import java.util.List;

/**
 * Interfaz del servicio para gestión de notificaciones
 */
public interface NotificationService {
    
    /**
     * Crea una notificación de actualización de estado
     */
    Notification createStatusUpdateNotification(String packageId, String trackingNumber, 
                                                String recipientPhone, PackageStatus status);
    
    /**
     * Envía una notificación (simulado - solo guarda como enviada)
     */
    void sendNotification(String notificationId);
    
    /**
     * Obtiene todas las notificaciones de un paquete
     */
    List<Notification> getNotificationsByPackageId(String packageId);
    
    /**
     * Obtiene todas las notificaciones de un número de rastreo
     */
    List<Notification> getNotificationsByTrackingNumber(String trackingNumber);
    
    /**
     * Obtiene todas las notificaciones pendientes
     */
    List<Notification> getPendingNotifications();
}

