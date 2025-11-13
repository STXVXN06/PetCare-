package com.stxvxn.app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un paquete en el sistema de logística
 */
@Document(collection = "packages")
public class Package {
    
    @Id
    private String id;
    
    private String trackingNumber; // Número de rastreo único
    private String recipientName; // Nombre del destinatario
    private String recipientAddress; // Dirección de entrega
    private String recipientPhone; // Teléfono del destinatario
    private String senderName; // Nombre del remitente
    private String senderAddress; // Dirección del remitente
    
    private PackageStatus status; // Estado actual del paquete
    private String currentLocation; // Ubicación actual (simulada)
    
    private List<StatusHistory> statusHistory; // Historial de cambios de estado
    private List<String> notes; // Notas adicionales
    
    private LocalDateTime createdAt; // Fecha de creación
    private LocalDateTime updatedAt; // Última actualización
    private LocalDateTime estimatedDelivery; // Fecha estimada de entrega
    
    // Constructor vacío (requerido por MongoDB)
    public Package() {
        this.status = PackageStatus.REGISTRADO;
        this.statusHistory = new ArrayList<>();
        this.notes = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Package(String trackingNumber, String recipientName, String recipientAddress, 
                   String recipientPhone, String senderName, String senderAddress) {
        this();
        this.trackingNumber = trackingNumber;
        this.recipientName = recipientName;
        this.recipientAddress = recipientAddress;
        this.recipientPhone = recipientPhone;
        this.senderName = senderName;
        this.senderAddress = senderAddress;
        this.currentLocation = "Almacén de origen";
    }
    
    /**
     * Clase interna para el historial de estados
     */
    public static class StatusHistory {
        private PackageStatus status;
        private LocalDateTime timestamp;
        private String updatedBy; // Empleado que actualizó
        private String notes;
        
        public StatusHistory() {
            this.timestamp = LocalDateTime.now();
        }
        
        public StatusHistory(PackageStatus status, String updatedBy, String notes) {
            this();
            this.status = status;
            this.updatedBy = updatedBy;
            this.notes = notes;
        }
        
        // Getters y Setters
        public PackageStatus getStatus() {
            return status;
        }
        
        public void setStatus(PackageStatus status) {
            this.status = status;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
        
        public String getUpdatedBy() {
            return updatedBy;
        }
        
        public void setUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
        }
        
        public String getNotes() {
            return notes;
        }
        
        public void setNotes(String notes) {
            this.notes = notes;
        }
    }
    
    // Getters y Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTrackingNumber() {
        return trackingNumber;
    }
    
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    
    public String getRecipientName() {
        return recipientName;
    }
    
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
    
    public String getRecipientAddress() {
        return recipientAddress;
    }
    
    public void setRecipientAddress(String recipientAddress) {
        this.recipientAddress = recipientAddress;
    }
    
    public String getRecipientPhone() {
        return recipientPhone;
    }
    
    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getSenderAddress() {
        return senderAddress;
    }
    
    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }
    
    public PackageStatus getStatus() {
        return status;
    }
    
    public void setStatus(PackageStatus status) {
        this.status = status;
    }
    
    public String getCurrentLocation() {
        return currentLocation;
    }
    
    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
    
    public List<StatusHistory> getStatusHistory() {
        return statusHistory;
    }
    
    public void setStatusHistory(List<StatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
    }
    
    public List<String> getNotes() {
        return notes;
    }
    
    public void setNotes(List<String> notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getEstimatedDelivery() {
        return estimatedDelivery;
    }
    
    public void setEstimatedDelivery(LocalDateTime estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }
    
    /**
     * Agrega un nuevo estado al historial
     */
    public void addStatusHistory(PackageStatus newStatus, String updatedBy, String notes) {
        StatusHistory history = new StatusHistory(newStatus, updatedBy, notes);
        this.statusHistory.add(history);
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
}

