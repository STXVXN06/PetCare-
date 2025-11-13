package com.stxvxn.app.service;

import com.stxvxn.app.dto.PackageResponse;
import com.stxvxn.app.model.Package;
import com.stxvxn.app.model.PackageStatus;
import com.stxvxn.app.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * Interfaz del servicio para rastreo simulado en tiempo real
 */
public interface TrackingService {
    
    /**
     * Obtiene la información actual de rastreo de un paquete
     */
    Optional<PackageResponse> getTrackingInfo(String trackingNumber);
    
    /**
     * Simula la actualización automática del estado de un paquete
     * (para demostración - actualiza el estado automáticamente)
     */
    CompletableFuture<Void> simulatePackageProgress(String trackingNumber);
    
    /**
     * Obtiene todos los paquetes en tránsito para simulación
     */
    List<PackageResponse> getPackagesInTransit();
}

