package com.stxvxn.app.controller;

import com.stxvxn.app.dto.CreatePackageRequest;
import com.stxvxn.app.dto.PackageResponse;
import com.stxvxn.app.dto.response.PageResponse;
import com.stxvxn.app.exception.PackageNotFoundException;
import com.stxvxn.app.model.PackageStatus;
import com.stxvxn.app.service.PackageService;
import com.stxvxn.app.util.Constants;
import com.stxvxn.app.util.ResponseBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de paquetes.
 * Maneja todas las operaciones relacionadas con paquetes.
 */
@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
@Slf4j
public class PackageController {
    
    private final PackageService packageService;
    
    @Value("${instance.name:app}")
    private String instanceName;
    
    /**
     * Crear un nuevo paquete.
     * 
     * @param request Datos del paquete a crear
     * @return Respuesta con el paquete creado
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPackage(
            @Valid @RequestBody CreatePackageRequest request) {
        log.info("POST /api/packages - Creating package for: {}", request.getRecipientName());
        
        PackageResponse response = packageService.createPackage(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseBuilder.success(response, instanceName));
    }
    
    /**
     * Obtener un paquete por ID.
     * 
     * @param id ID del paquete
     * @return Respuesta con el paquete encontrado
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPackageById(@PathVariable String id) {
        log.debug("GET /api/packages/{} - Finding package by ID", id);
        
        PackageResponse response = packageService.findById(id)
                .orElseThrow(() -> new PackageNotFoundException(id, true));
        
        return ResponseEntity.ok(ResponseBuilder.success(response, instanceName));
    }
    
    /**
     * Obtener un paquete por número de rastreo.
     * 
     * @param trackingNumber Número de rastreo del paquete
     * @return Respuesta con el paquete encontrado
     */
    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<Map<String, Object>> getPackageByTrackingNumber(
            @PathVariable String trackingNumber) {
        log.debug("GET /api/packages/tracking/{} - Finding package by tracking number", trackingNumber);
        
        PackageResponse response = packageService.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new PackageNotFoundException(trackingNumber));
        
        return ResponseEntity.ok(ResponseBuilder.success(response, instanceName));
    }
    
    /**
     * Obtener todos los paquetes.
     * Soporta paginación opcional mediante parámetros de query.
     * 
     * @param page Número de página (opcional, default: 0)
     * @param size Tamaño de página (opcional, default: 20, max: 100)
     * @param sort Campo para ordenar (opcional, default: createdAt)
     * @param direction Dirección de ordenamiento (opcional, default: DESC)
     * @return Respuesta con la lista de paquetes o respuesta paginada
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPackages(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "DESC") String direction) {
        
        // Si se proporcionan parámetros de paginación, usar paginación
        if (page != null || size != null) {
            log.info("GET /api/packages - Getting all packages with pagination: page={}, size={}", 
                     page != null ? page : 0, size != null ? size : Constants.DEFAULT_PAGE_SIZE);
            
            int pageNumber = page != null ? page : 0;
            int pageSize = size != null ? Math.min(size, Constants.MAX_PAGE_SIZE) : Constants.DEFAULT_PAGE_SIZE;
            
            Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction) 
                    ? Sort.Direction.ASC 
                    : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, sort));
            
            PageResponse<PackageResponse> pageResponse = packageService.findAll(pageable);
            Map<String, Object> response = ResponseBuilder.success(pageResponse.getContent(), instanceName);
            response.put("pagination", Map.of(
                "page", pageResponse.getPage(),
                "size", pageResponse.getSize(),
                "totalElements", pageResponse.getTotalElements(),
                "totalPages", pageResponse.getTotalPages(),
                "hasNext", pageResponse.isHasNext(),
                "hasPrevious", pageResponse.isHasPrevious()
            ));
            
            return ResponseEntity.ok(response);
        }
        
        // Sin paginación (comportamiento original)
        log.info("GET /api/packages - Getting all packages");
        List<PackageResponse> packages = packageService.findAll();
        Map<String, Object> response = ResponseBuilder.success(packages, instanceName);
        response.put("count", packages.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtener paquetes por estado.
     * Soporta paginación opcional mediante parámetros de query.
     * 
     * @param status Estado del paquete
     * @param page Número de página (opcional, default: 0)
     * @param size Tamaño de página (opcional, default: 20, max: 100)
     * @param sort Campo para ordenar (opcional, default: createdAt)
     * @param direction Dirección de ordenamiento (opcional, default: DESC)
     * @return Respuesta con la lista de paquetes filtrados por estado o respuesta paginada
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getPackagesByStatus(
            @PathVariable String status,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "createdAt") String sort,
            @RequestParam(required = false, defaultValue = "DESC") String direction) {
        
        PackageStatus packageStatus = PackageStatus.valueOf(status.toUpperCase());
        
        // Si se proporcionan parámetros de paginación, usar paginación
        if (page != null || size != null) {
            log.info("GET /api/packages/status/{} - Getting packages by status with pagination: page={}, size={}", 
                     status, page != null ? page : 0, size != null ? size : Constants.DEFAULT_PAGE_SIZE);
            
            int pageNumber = page != null ? page : 0;
            int pageSize = size != null ? Math.min(size, Constants.MAX_PAGE_SIZE) : Constants.DEFAULT_PAGE_SIZE;
            
            Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction) 
                    ? Sort.Direction.ASC 
                    : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, sort));
            
            PageResponse<PackageResponse> pageResponse = packageService.findByStatus(packageStatus, pageable);
            Map<String, Object> response = ResponseBuilder.success(pageResponse.getContent(), instanceName);
            response.put("pagination", Map.of(
                "page", pageResponse.getPage(),
                "size", pageResponse.getSize(),
                "totalElements", pageResponse.getTotalElements(),
                "totalPages", pageResponse.getTotalPages(),
                "hasNext", pageResponse.isHasNext(),
                "hasPrevious", pageResponse.isHasPrevious()
            ));
            response.put("status", status);
            
            return ResponseEntity.ok(response);
        }
        
        // Sin paginación (comportamiento original)
        log.info("GET /api/packages/status/{} - Getting packages by status", status);
        List<PackageResponse> packages = packageService.findByStatus(packageStatus);
        
        Map<String, Object> response = ResponseBuilder.success(packages, instanceName);
        response.put("count", packages.size());
        response.put("status", status);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtener paquetes por teléfono del destinatario.
     * 
     * @param phone Teléfono del destinatario
     * @return Respuesta con la lista de paquetes del destinatario
     */
    @GetMapping("/recipient/{phone}")
    public ResponseEntity<Map<String, Object>> getPackagesByRecipientPhone(
            @PathVariable String phone) {
        log.info("GET /api/packages/recipient/{} - Getting packages by recipient phone", phone);
        
        List<PackageResponse> packages = packageService.findByRecipientPhone(phone);
        Map<String, Object> response = ResponseBuilder.success(packages, instanceName);
        response.put("count", packages.size());
        response.put("phone", phone);
        
        return ResponseEntity.ok(response);
    }
}

