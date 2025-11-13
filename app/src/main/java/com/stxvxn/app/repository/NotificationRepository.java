package com.stxvxn.app.repository;

import com.stxvxn.app.model.Notification;
import com.stxvxn.app.model.Notification.NotificationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Notification
 */
@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    /**
     * Busca notificaciones por ID de paquete
     */
    List<Notification> findByPackageId(String packageId);
    
    /**
     * Busca notificaciones por número de rastreo
     */
    List<Notification> findByTrackingNumber(String trackingNumber);
    
    /**
     * Busca notificaciones por teléfono del destinatario
     */
    List<Notification> findByRecipientPhone(String recipientPhone);
    
    /**
     * Busca notificaciones por estado
     */
    List<Notification> findByStatus(NotificationStatus status);
    
    /**
     * Busca notificaciones pendientes
     */
    List<Notification> findByStatusOrderByCreatedAtDesc(NotificationStatus status);
}

