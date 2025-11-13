package com.stxvxn.app.controller;

import com.stxvxn.app.dto.PackageResponse;
import com.stxvxn.app.dto.UpdateStatusRequest;
import com.stxvxn.app.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para empleados (actualización de estados de paquetes)
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    
    @Autowired
    private PackageService packageService;
    
    @Value("${instance.name:app}")
    private String instanceName;
    
    /**
     * Actualizar el estado de un paquete (para empleados)
     */
    @PutMapping("/packages/{trackingNumber}/status")
    public ResponseEntity<?> updatePackageStatus(
            @PathVariable String trackingNumber,
            @RequestBody UpdateStatusRequest request) {
        try {
            // Validar que se proporcione el empleado
            if (request.getUpdatedBy() == null || request.getUpdatedBy().trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("error", "El campo 'updatedBy' es requerido");
                error.put("instance", instanceName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            Optional<PackageResponse> pkgOpt = packageService.updateStatus(trackingNumber, request);
            
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
            result.put("message", "Estado actualizado exitosamente");
            result.put("data", pkgOpt.get());
            result.put("instance", instanceName);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Transición de estado inválida");
            error.put("message", e.getMessage());
            error.put("instance", instanceName);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al actualizar estado");
            error.put("message", e.getMessage());
            error.put("instance", instanceName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

