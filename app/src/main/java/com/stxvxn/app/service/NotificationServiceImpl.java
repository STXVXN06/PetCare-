package com.stxvxn.app.service;

import com.stxvxn.app.model.Notification;
import com.stxvxn.app.model.Notification.NotificationStatus;
import com.stxvxn.app.model.Notification.NotificationType;
import com.stxvxn.app.model.PackageStatus;
import com.stxvxn.app.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación del servicio para gestión de notificaciones
 */
@Service
public class NotificationServiceImpl implements NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Override
    @Transactional
    public Notification createStatusUpdateNotification(String packageId, String trackingNumber, 
                                                       String recipientPhone, PackageStatus status) {
        // Crear mensaje según el estado
        String message = buildMessageForStatus(trackingNumber, status);
        
        // Determinar tipo de notificación
        NotificationType type = determineNotificationType(status);
        
        // Crear notificación
        Notification notification = new Notification(
            packageId,
            trackingNumber,
            recipientPhone,
            message,
            type
        );
        
        // Guardar notificación
        Notification saved = notificationRepository.save(notification);
        
        // Simular envío inmediato (en producción sería asíncrono)
        sendNotification(saved.getId());
        
        return saved;
    }
    
    @Override
    @Transactional
    public void sendNotification(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
            
            // En producción aquí se enviaría SMS, Email, Push, etc.
            System.out.println("📧 Notificación enviada: " + notification.getMessage());
        });
    }
    
    @Override
    public List<Notification> getNotificationsByPackageId(String packageId) {
        return notificationRepository.findByPackageId(packageId);
    }
    
    @Override
    public List<Notification> getNotificationsByTrackingNumber(String trackingNumber) {
        return notificationRepository.findByTrackingNumber(trackingNumber);
    }
    
    @Override
    public List<Notification> getPendingNotifications() {
        return notificationRepository.findByStatusOrderByCreatedAtDesc(NotificationStatus.PENDING);
    }
    
    /**
     * Construye el mensaje según el estado del paquete
     */
    private String buildMessageForStatus(String trackingNumber, PackageStatus status) {
        String baseMessage = "Su paquete con número de rastreo " + trackingNumber;
        
        switch (status) {
            case REGISTRADO:
                return baseMessage + " ha sido registrado en nuestro sistema.";
            case EN_ALMACEN:
                return baseMessage + " está en nuestro almacén de origen.";
            case EN_TRANSITO:
                return baseMessage + " está en tránsito hacia su destino.";
            case EN_DISTRIBUCION:
                return baseMessage + " está en el centro de distribución local.";
            case EN_REPARTO:
                return baseMessage + " está siendo entregado. Por favor esté atento.";
            case ENTREGADO:
                return baseMessage + " ha sido entregado exitosamente. ¡Gracias por su preferencia!";
            case DEVUELTO:
                return baseMessage + " ha sido devuelto. Por favor contacte con atención al cliente.";
            case PERDIDO:
                return baseMessage + " ha sido reportado como perdido. Estamos investigando. Contacte con atención al cliente.";
            default:
                return baseMessage + " ha cambiado de estado.";
        }
    }
    
    /**
     * Determina el tipo de notificación según el estado
     */
    private NotificationType determineNotificationType(PackageStatus status) {
        switch (status) {
            case ENTREGADO:
                return NotificationType.DELIVERY_CONFIRMED;
            case PERDIDO:
            case DEVUELTO:
                return NotificationType.EXCEPTION;
            default:
                return NotificationType.STATUS_UPDATE;
        }
    }
}

