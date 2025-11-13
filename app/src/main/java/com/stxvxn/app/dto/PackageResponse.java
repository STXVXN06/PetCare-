package com.stxvxn.app.dto;

import com.stxvxn.app.model.Package;
import com.stxvxn.app.model.PackageStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para respuesta de información de paquete
 */
public class PackageResponse {
    private String id;
    private String trackingNumber;
    private String recipientName;
    private String recipientAddress;
    private String recipientPhone;
    private String senderName;
    private String senderAddress;
    private PackageStatus status;
    private String currentLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime estimatedDelivery;
    private List<StatusHistoryResponse> statusHistory;
    
    // Constructor vacío
    public PackageResponse() {
    }
    
    // Constructor desde entidad Package
    public PackageResponse(Package pkg) {
        this.id = pkg.getId();
        this.trackingNumber = pkg.getTrackingNumber();
        this.recipientName = pkg.getRecipientName();
        this.recipientAddress = pkg.getRecipientAddress();
        this.recipientPhone = pkg.getRecipientPhone();
        this.senderName = pkg.getSenderName();
        this.senderAddress = pkg.getSenderAddress();
        this.status = pkg.getStatus();
        this.currentLocation = pkg.getCurrentLocation();
        this.createdAt = pkg.getCreatedAt();
        this.updatedAt = pkg.getUpdatedAt();
        this.estimatedDelivery = pkg.getEstimatedDelivery();
        
        if (pkg.getStatusHistory() != null) {
            this.statusHistory = pkg.getStatusHistory().stream()
                    .map(StatusHistoryResponse::new)
                    .collect(Collectors.toList());
        }
    }
    
    /**
     * Clase interna para el historial de estados en la respuesta
     */
    public static class StatusHistoryResponse {
        private PackageStatus status;
        private LocalDateTime timestamp;
        private String updatedBy;
        private String notes;
        
        public StatusHistoryResponse() {
        }
        
        public StatusHistoryResponse(Package.StatusHistory history) {
            this.status = history.getStatus();
            this.timestamp = history.getTimestamp();
            this.updatedBy = history.getUpdatedBy();
            this.notes = history.getNotes();
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
    
    public List<StatusHistoryResponse> getStatusHistory() {
        return statusHistory;
    }
    
    public void setStatusHistory(List<StatusHistoryResponse> statusHistory) {
        this.statusHistory = statusHistory;
    }
}

