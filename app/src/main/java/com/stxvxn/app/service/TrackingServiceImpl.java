package com.stxvxn.app.service;

import com.stxvxn.app.dto.PackageResponse;
import com.stxvxn.app.dto.UpdateStatusRequest;
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
import java.util.stream.Collectors;

/**
 * Implementación del servicio para rastreo simulado en tiempo real
 */
@Service
public class TrackingServiceImpl implements TrackingService {
    
    @Autowired
    private PackageRepository packageRepository;
    
    @Autowired
    private PackageService packageService;
    
    private final Random random = new Random();
    
    @Override
    public Optional<PackageResponse> getTrackingInfo(String trackingNumber) {
        return packageService.findByTrackingNumber(trackingNumber);
    }
    
    @Override
    @Async
    public CompletableFuture<Void> simulatePackageProgress(String trackingNumber) {
        return CompletableFuture.runAsync(() -> {
            try {
                Optional<Package> pkgOpt = packageRepository.findByTrackingNumber(trackingNumber);
                
                if (pkgOpt.isEmpty()) {
                    return;
                }
                
                Package pkg = pkgOpt.get();
                PackageStatus currentStatus = pkg.getStatus();
                
                // Solo simular si el paquete no está en estado final
                if (currentStatus == PackageStatus.ENTREGADO || 
                    currentStatus == PackageStatus.DEVUELTO || 
                    currentStatus == PackageStatus.PERDIDO) {
                    return;
                }
                
                // Simular progreso automático con delays
                PackageStatus nextStatus = getNextStatus(currentStatus);
                
                if (nextStatus != null) {
                    // Esperar un tiempo aleatorio (simulando tiempo de tránsito)
                    int delaySeconds = 5 + random.nextInt(10); // Entre 5 y 15 segundos
                    
                    try {
                        Thread.sleep(delaySeconds * 1000L);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    
                    // Actualizar estado
                    UpdateStatusRequest updateRequest = new UpdateStatusRequest();
                    updateRequest.setStatus(nextStatus);
                    updateRequest.setUpdatedBy("Sistema Automático");
                    updateRequest.setNotes("Actualización automática de estado");
                    
                    packageService.updateStatus(trackingNumber, updateRequest);
                }
            } catch (Exception e) {
                System.err.println("Error en simulación de progreso: " + e.getMessage());
            }
        });
    }
    
    @Override
    public List<PackageResponse> getPackagesInTransit() {
        List<PackageStatus> transitStatuses = List.of(
            PackageStatus.EN_ALMACEN,
            PackageStatus.EN_TRANSITO,
            PackageStatus.EN_DISTRIBUCION,
            PackageStatus.EN_REPARTO
        );
        
        return transitStatuses.stream()
                .flatMap(status -> packageRepository.findByStatus(status).stream())
                .map(PackageResponse::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene el siguiente estado lógico según el estado actual
     */
    private PackageStatus getNextStatus(PackageStatus currentStatus) {
        switch (currentStatus) {
            case REGISTRADO:
                return PackageStatus.EN_ALMACEN;
            case EN_ALMACEN:
                return PackageStatus.EN_TRANSITO;
            case EN_TRANSITO:
                return PackageStatus.EN_DISTRIBUCION;
            case EN_DISTRIBUCION:
                return PackageStatus.EN_REPARTO;
            case EN_REPARTO:
                // 90% de probabilidad de entrega exitosa, 10% de devolución
                return random.nextDouble() < 0.9 ? PackageStatus.ENTREGADO : PackageStatus.DEVUELTO;
            default:
                return null; // Estado final
        }
    }
}

