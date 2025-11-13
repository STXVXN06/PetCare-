package com.stxvxn.app.dto;

import com.stxvxn.app.model.PackageStatus;

/**
 * DTO para actualizar el estado de un paquete
 */
public class UpdateStatusRequest {
    private PackageStatus status;
    private String updatedBy; // Empleado que actualiza
    private String notes; // Notas adicionales
    private String location; // Ubicación actual (opcional)
    
    // Constructor vacío
    public UpdateStatusRequest() {
    }
    
    // Getters y Setters
    public PackageStatus getStatus() {
        return status;
    }
    
    public void setStatus(PackageStatus status) {
        this.status = status;
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
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
}

