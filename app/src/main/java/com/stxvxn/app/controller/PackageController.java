package com.stxvxn.app.controller;

import com.stxvxn.app.dto.CreatePackageRequest;
import com.stxvxn.app.dto.PackageResponse;
import com.stxvxn.app.dto.UpdateStatusRequest;
import com.stxvxn.app.model.PackageStatus;
import com.stxvxn.app.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para gestión de paquetes
 */
@RestController
@RequestMapping("/api/packages")
public class PackageController {
    
    @Autowired
    private PackageService packageService;
    
    @Value("${instance.name:app}")
    private String instanceName;
    
    /**
     * Crear un nuevo paquete
     */
    @PostMapping
    public ResponseEntity<?> createPackage(@RequestBody CreatePackageRequest request) {
        try {
            PackageResponse response = packageService.createPackage(request);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            result.put("instance", instanceName);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            error.put("instance", instanceName);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al crear paquete");
            error.put("message", e.getMessage());
            error.put("instance", instanceName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Obtener un paquete por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPackageById(@PathVariable String id) {
        Optional<PackageResponse> pkgOpt = packageService.findById(id);
        
        if (pkgOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Paquete no encontrado");
            error.put("id", id);
            error.put("instance", instanceName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", pkgOpt.get());
        result.put("instance", instanceName);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Obtener un paquete por número de rastreo
     */
    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<?> getPackageByTrackingNumber(@PathVariable String trackingNumber) {
        Optional<PackageResponse> pkgOpt = packageService.findByTrackingNumber(trackingNumber);
        
        if (pkgOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Paquete no encontrado");
            error.put("trackingNumber", trackingNumber);
            error.put("instance", instanceName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", pkgOpt.get());
        result.put("instance", instanceName);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Obtener todos los paquetes
     */
    @GetMapping
    public ResponseEntity<?> getAllPackages() {
        try {
            List<PackageResponse> packages = packageService.findAll();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", packages);
            result.put("count", packages.size());
            result.put("instance", instanceName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al obtener paquetes");
            error.put("message", e.getMessage());
            error.put("instance", instanceName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Obtener paquetes por estado
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getPackagesByStatus(@PathVariable String status) {
        try {
            PackageStatus packageStatus = PackageStatus.valueOf(status.toUpperCase());
            List<PackageResponse> packages = packageService.findByStatus(packageStatus);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", packages);
            result.put("count", packages.size());
            result.put("status", status);
            result.put("instance", instanceName);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Estado inválido");
            error.put("validStatuses", List.of("REGISTRADO", "EN_ALMACEN", "EN_TRANSITO", 
                    "EN_DISTRIBUCION", "EN_REPARTO", "ENTREGADO", "DEVUELTO", "PERDIDO"));
            error.put("instance", instanceName);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al obtener paquetes");
            error.put("message", e.getMessage());
            error.put("instance", instanceName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Obtener paquetes por teléfono del destinatario
     */
    @GetMapping("/recipient/{phone}")
    public ResponseEntity<?> getPackagesByRecipientPhone(@PathVariable String phone) {
        try {
            List<PackageResponse> packages = packageService.findByRecipientPhone(phone);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", packages);
            result.put("count", packages.size());
            result.put("phone", phone);
            result.put("instance", instanceName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al obtener paquetes");
            error.put("message", e.getMessage());
            error.put("instance", instanceName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

