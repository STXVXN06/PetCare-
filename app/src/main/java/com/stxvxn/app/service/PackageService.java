package com.stxvxn.app.service;

import com.stxvxn.app.dto.CreatePackageRequest;
import com.stxvxn.app.dto.PackageResponse;
import com.stxvxn.app.dto.UpdateStatusRequest;
import com.stxvxn.app.dto.response.PageResponse;
import com.stxvxn.app.model.Package;
import com.stxvxn.app.model.PackageStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio para gestión de paquetes
 */
public interface PackageService {
    
    /**
     * Crea un nuevo paquete
     */
    PackageResponse createPackage(CreatePackageRequest request);
    
    /**
     * Busca un paquete por ID
     */
    Optional<PackageResponse> findById(String id);
    
    /**
     * Busca un paquete por número de rastreo
     */
    Optional<PackageResponse> findByTrackingNumber(String trackingNumber);
    
    /**
     * Obtiene todos los paquetes
     */
    List<PackageResponse> findAll();
    
    /**
     * Obtiene todos los paquetes con paginación.
     * 
     * @param pageable Parámetros de paginación
     * @return Respuesta paginada con paquetes
     */
    PageResponse<PackageResponse> findAll(Pageable pageable);
    
    /**
     * Busca paquetes por estado
     */
    List<PackageResponse> findByStatus(PackageStatus status);
    
    /**
     * Busca paquetes por estado con paginación.
     * 
     * @param status Estado del paquete
     * @param pageable Parámetros de paginación
     * @return Respuesta paginada con paquetes
     */
    PageResponse<PackageResponse> findByStatus(PackageStatus status, Pageable pageable);
    
    /**
     * Busca paquetes por teléfono del destinatario
     */
    List<PackageResponse> findByRecipientPhone(String phone);
    
    /**
     * Actualiza el estado de un paquete
     */
    Optional<PackageResponse> updateStatus(String trackingNumber, UpdateStatusRequest request);
    
    /**
     * Genera un número de rastreo único
     */
    String generateTrackingNumber();
}

