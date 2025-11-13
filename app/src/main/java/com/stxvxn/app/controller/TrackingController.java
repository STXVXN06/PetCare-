package com.stxvxn.app.controller;

import com.stxvxn.app.dto.PackageResponse;
import com.stxvxn.app.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Controlador REST para rastreo de paquetes en tiempo real
 */
@RestController
@RequestMapping("/api/tracking")
public class TrackingController {
    
    @Autowired
    private TrackingService trackingService;
    
    @Value("${instance.name:app}")
    private String instanceName;
    
    /**
     * Obtener información de rastreo de un paquete
     */
    @GetMapping("/{trackingNumber}")
    public ResponseEntity<?> getTrackingInfo(@PathVariable String trackingNumber) {
        Optional<PackageResponse> pkgOpt = trackingService.getTrackingInfo(trackingNumber);
        
        if (pkgOpt.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Paquete no encontrado");
            error.put("trackingNumber", trackingNumber);
            error.put("instance", instanceName);
            return ResponseEntity.status(404).body(error);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", pkgOpt.get());
        result.put("instance", instanceName);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Iniciar simulación de progreso automático de un paquete
     */
    @PostMapping("/{trackingNumber}/simulate")
    public ResponseEntity<?> simulateProgress(@PathVariable String trackingNumber) {
        try {
            CompletableFuture<Void> future = trackingService.simulatePackageProgress(trackingNumber);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Simulación de progreso iniciada");
            result.put("trackingNumber", trackingNumber);
            result.put("instance", instanceName);
            
            // No esperar a que termine, devolver inmediatamente
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al iniciar simulación");
            error.put("message", e.getMessage());
            error.put("instance", instanceName);
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * Obtener todos los paquetes en tránsito
     */
    @GetMapping("/in-transit")
    public ResponseEntity<?> getPackagesInTransit() {
        try {
            List<PackageResponse> packages = trackingService.getPackagesInTransit();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", packages);
            result.put("count", packages.size());
            result.put("instance", instanceName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al obtener paquetes en tránsito");
            error.put("message", e.getMessage());
            error.put("instance", instanceName);
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * Endpoint SSE (Server-Sent Events) para rastreo en tiempo real
     * Permite al cliente recibir actualizaciones automáticas
     */
    @GetMapping(value = "/{trackingNumber}/stream", produces = "text/event-stream")
    public SseEmitter streamTracking(@PathVariable String trackingNumber) {
        SseEmitter emitter = new SseEmitter(300000L); // Timeout de 5 minutos
        
        // Iniciar simulación en segundo plano
        trackingService.simulatePackageProgress(trackingNumber);
        
        // Enviar actualizaciones periódicas
        CompletableFuture.runAsync(() -> {
            try {
                for (int i = 0; i < 20; i++) { // Máximo 20 actualizaciones
                    Thread.sleep(3000); // Cada 3 segundos
                    
                    Optional<PackageResponse> pkgOpt = trackingService.getTrackingInfo(trackingNumber);
                    if (pkgOpt.isPresent()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("trackingNumber", trackingNumber);
                        data.put("status", pkgOpt.get().getStatus());
                        data.put("currentLocation", pkgOpt.get().getCurrentLocation());
                        data.put("updatedAt", pkgOpt.get().getUpdatedAt());
                        
                        emitter.send(SseEmitter.event()
                                .name("update")
                                .data(data));
                        
                        // Si el paquete está en estado final, terminar
                        if (pkgOpt.get().getStatus() == com.stxvxn.app.model.PackageStatus.ENTREGADO ||
                            pkgOpt.get().getStatus() == com.stxvxn.app.model.PackageStatus.DEVUELTO ||
                            pkgOpt.get().getStatus() == com.stxvxn.app.model.PackageStatus.PERDIDO) {
                            emitter.complete();
                            return;
                        }
                    } else {
                        emitter.completeWithError(new RuntimeException("Paquete no encontrado"));
                        return;
                    }
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }
}

