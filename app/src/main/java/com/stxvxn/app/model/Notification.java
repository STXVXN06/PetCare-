package com.stxvxn.app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Entidad que representa una notificación enviada a un cliente
 */
@Document(collection = "notifications")
public class Notification {
    
    @Id
    private String id;
    
    private String packageId; // ID del paquete relacionado
    private String trackingNumber; // Número de rastreo del paquete
    private String recipientPhone; // Teléfono del destinatario
    private String recipientEmail; // Email del destinatario (opcional)
    
    private String message; // Mensaje de la notificación
    private NotificationType type; // Tipo de notificación
    private NotificationStatus status; // Estado de la notificación
    
    private LocalDateTime createdAt; // Fecha de creación
    private LocalDateTime sentAt; // Fecha de envío (si se envió)
    
    // Constructor vacío (requerido por MongoDB)
    public Notification() {
        this.status = NotificationStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    public Notification(String packageId, String trackingNumber, String recipientPhone, 
                       String message, NotificationType type) {
        this();
        this.packageId = packageId;
        this.trackingNumber = trackingNumber;
        this.recipientPhone = recipientPhone;
        this.message = message;
        this.type = type;
    }
    
    /**
     * Enum para tipos de notificación
     */
    public enum NotificationType {
        STATUS_UPDATE("Actualización de Estado"),
        DELIVERY_CONFIRMED("Entrega Confirmada"),
        DELAY_ALERT("Alerta de Retraso"),
        EXCEPTION("Excepción");
        
        private final String displayName;
        
        NotificationType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Enum para estado de la notificación
     */
    public enum NotificationStatus {
        PENDING("Pendiente"),
        SENT("Enviada"),
        FAILED("Fallida"),
        DELIVERED("Entregada");
        
        private final String displayName;
        
        NotificationStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Getters y Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPackageId() {
        return packageId;
    }
    
    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }
    
    public String getTrackingNumber() {
        return trackingNumber;
    }
    
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    
    public String getRecipientPhone() {
        return recipientPhone;
    }
    
    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }
    
    public String getRecipientEmail() {
        return recipientEmail;
    }
    
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public NotificationStatus getStatus() {
        return status;
    }
    
    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}

