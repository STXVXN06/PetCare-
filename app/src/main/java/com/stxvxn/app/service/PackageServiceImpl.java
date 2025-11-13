package com.stxvxn.app.service;

import com.stxvxn.app.dto.CreatePackageRequest;
import com.stxvxn.app.dto.PackageResponse;
import com.stxvxn.app.dto.UpdateStatusRequest;
import com.stxvxn.app.model.Package;
import com.stxvxn.app.model.PackageStatus;
import com.stxvxn.app.repository.PackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para gestión de paquetes
 */
@Service
public class PackageServiceImpl implements PackageService {
    
    @Autowired
    private PackageRepository packageRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Override
    @Transactional
    public PackageResponse createPackage(CreatePackageRequest request) {
        // Validar datos
        if (request.getRecipientName() == null || request.getRecipientName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del destinatario es requerido");
        }
        if (request.getRecipientPhone() == null || request.getRecipientPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono del destinatario es requerido");
        }
        
        // Generar número de rastreo único
        String trackingNumber = generateTrackingNumber();
        
        // Crear nuevo paquete
        Package pkg = new Package(
            trackingNumber,
            request.getRecipientName(),
            request.getRecipientAddress(),
            request.getRecipientPhone(),
            request.getSenderName(),
            request.getSenderAddress()
        );
        
        // Establecer fecha estimada de entrega (3 días desde ahora)
        pkg.setEstimatedDelivery(LocalDateTime.now().plusDays(3));
        
        // Guardar en base de datos
        Package savedPackage = packageRepository.save(pkg);
        
        // Crear notificación inicial
        notificationService.createStatusUpdateNotification(
            savedPackage.getId(),
            savedPackage.getTrackingNumber(),
            savedPackage.getRecipientPhone(),
            savedPackage.getStatus()
        );
        
        return new PackageResponse(savedPackage);
    }
    
    @Override
    public Optional<PackageResponse> findById(String id) {
        return packageRepository.findById(id)
                .map(PackageResponse::new);
    }
    
    @Override
    public Optional<PackageResponse> findByTrackingNumber(String trackingNumber) {
        return packageRepository.findByTrackingNumber(trackingNumber)
                .map(PackageResponse::new);
    }
    
    @Override
    public List<PackageResponse> findAll() {
        return packageRepository.findAll().stream()
                .map(PackageResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PackageResponse> findByStatus(PackageStatus status) {
        return packageRepository.findByStatus(status).stream()
                .map(PackageResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PackageResponse> findByRecipientPhone(String phone) {
        return packageRepository.findByRecipientPhone(phone).stream()
                .map(PackageResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public Optional<PackageResponse> updateStatus(String trackingNumber, UpdateStatusRequest request) {
        Optional<Package> pkgOpt = packageRepository.findByTrackingNumber(trackingNumber);
        
        if (pkgOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Package pkg = pkgOpt.get();
        PackageStatus newStatus = request.getStatus();
        
        // Validar transición de estado
        if (!pkg.getStatus().canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                "No se puede cambiar de " + pkg.getStatus() + " a " + newStatus
            );
        }
        
        // Actualizar ubicación si se proporciona
        if (request.getLocation() != null && !request.getLocation().trim().isEmpty()) {
            pkg.setCurrentLocation(request.getLocation());
        } else {
            // Actualizar ubicación según el estado (simulado)
            pkg.setCurrentLocation(getLocationForStatus(newStatus));
        }
        
        // Agregar al historial
        String updatedBy = request.getUpdatedBy() != null ? request.getUpdatedBy() : "Sistema";
        String notes = request.getNotes() != null ? request.getNotes() : "";
        pkg.addStatusHistory(newStatus, updatedBy, notes);
        
        // Guardar cambios
        Package updatedPackage = packageRepository.save(pkg);
        
        // Crear notificación de cambio de estado
        notificationService.createStatusUpdateNotification(
            updatedPackage.getId(),
            updatedPackage.getTrackingNumber(),
            updatedPackage.getRecipientPhone(),
            newStatus
        );
        
        return Optional.of(new PackageResponse(updatedPackage));
    }
    
    @Override
    public String generateTrackingNumber() {
        // Generar número de rastreo único (formato: TRK-XXXXXXXX)
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        String trackingNumber = "TRK-" + uuid;
        
        // Verificar que no exista
        while (packageRepository.findByTrackingNumber(trackingNumber).isPresent()) {
            uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            trackingNumber = "TRK-" + uuid;
        }
        
        return trackingNumber;
    }
    
    /**
     * Obtiene la ubicación simulada según el estado
     */
    private String getLocationForStatus(PackageStatus status) {
        switch (status) {
            case REGISTRADO:
                return "Almacén de origen";
            case EN_ALMACEN:
                return "Almacén de origen";
            case EN_TRANSITO:
                return "Centro de distribución principal";
            case EN_DISTRIBUCION:
                return "Centro de distribución local";
            case EN_REPARTO:
                return "En ruta de entrega";
            case ENTREGADO:
                return "Entregado al destinatario";
            case DEVUELTO:
                return "En proceso de devolución";
            case PERDIDO:
                return "Ubicación desconocida";
            default:
                return "Ubicación no disponible";
        }
    }
}

