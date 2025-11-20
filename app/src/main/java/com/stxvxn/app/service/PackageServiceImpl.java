package com.stxvxn.app.service;

import com.stxvxn.app.dto.CreatePackageRequest;
import com.stxvxn.app.dto.PackageResponse;
import com.stxvxn.app.dto.UpdateStatusRequest;
import com.stxvxn.app.dto.response.PageResponse;
import com.stxvxn.app.exception.PackageNotFoundException;
import com.stxvxn.app.model.Package;
import com.stxvxn.app.model.PackageStatus;
import com.stxvxn.app.repository.PackageRepository;
import com.stxvxn.app.util.TrackingNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para gestión de paquetes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PackageServiceImpl implements PackageService {
    
    private final PackageRepository packageRepository;
    private final ValidationService validationService;
    private final TrackingNumberGenerator trackingNumberGenerator;
    private final EventPublisherService eventPublisherService;
    
    @Override
    @Transactional
    public PackageResponse createPackage(CreatePackageRequest request) {
        log.info("Creating package for recipient: {}", request.getRecipientName());
        
        // Validar datos usando ValidationService
        validationService.validatePackageData(request);
        
        // Generar número de rastreo único usando TrackingNumberGenerator
        String trackingNumber = trackingNumberGenerator.generateUnique(packageRepository);
        log.debug("Generated tracking number: {}", trackingNumber);
        
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
        log.info("Package created successfully with ID: {} and tracking: {}", 
                 savedPackage.getId(), savedPackage.getTrackingNumber());
        
        // Publicar evento (el listener se encargará de crear la notificación)
        eventPublisherService.publishPackageCreatedEvent(savedPackage);
        
        return new PackageResponse(savedPackage);
    }
    
    @Override
    public Optional<PackageResponse> findById(String id) {
        log.debug("Finding package by ID: {}", id);
        return packageRepository.findById(id)
                .map(PackageResponse::new);
    }
    
    @Override
    public Optional<PackageResponse> findByTrackingNumber(String trackingNumber) {
        log.debug("Finding package by tracking number: {}", trackingNumber);
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
    public PageResponse<PackageResponse> findAll(Pageable pageable) {
        log.debug("Finding all packages with pagination: page={}, size={}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Package> page = packageRepository.findAll(pageable);
        Page<PackageResponse> responsePage = page.map(PackageResponse::new);
        
        return new PageResponse<>(responsePage);
    }
    
    @Override
    public List<PackageResponse> findByStatus(PackageStatus status) {
        return packageRepository.findByStatus(status).stream()
                .map(PackageResponse::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public PageResponse<PackageResponse> findByStatus(PackageStatus status, Pageable pageable) {
        log.debug("Finding packages by status {} with pagination: page={}, size={}", 
                 status, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Package> page = packageRepository.findByStatus(status, pageable);
        Page<PackageResponse> responsePage = page.map(PackageResponse::new);
        
        return new PageResponse<>(responsePage);
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
        log.info("Updating status for tracking: {} to status: {} by employee: {}", 
                 trackingNumber, request.getStatus(), request.getUpdatedBy());
        
        // Buscar paquete
        Package pkg = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new PackageNotFoundException(trackingNumber));
        
        PackageStatus oldStatus = pkg.getStatus();
        PackageStatus newStatus = request.getStatus();
        
        // Validar transición de estado usando ValidationService
        validationService.validateStatusTransition(oldStatus, newStatus);
        
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
        log.info("Package status updated successfully: {} ({} -> {})", 
                 trackingNumber, oldStatus, newStatus);
        
        // Publicar evento (el listener se encargará de crear la notificación)
        eventPublisherService.publishStatusChangedEvent(
            updatedPackage, 
            oldStatus, 
            updatedBy
        );
        
        return Optional.of(new PackageResponse(updatedPackage));
    }
    
    @Override
    public String generateTrackingNumber() {
        // Este método ahora delega a TrackingNumberGenerator
        // Se mantiene por compatibilidad con la interfaz
        log.debug("Generating tracking number using TrackingNumberGenerator");
        return trackingNumberGenerator.generateUnique(packageRepository);
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

