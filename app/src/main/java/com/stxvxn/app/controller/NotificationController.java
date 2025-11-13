package com.stxvxn.app.controller;

import com.stxvxn.app.model.Notification;
import com.stxvxn.app.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para consulta de notificaciones
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Value("${instance.name:app}")
    private String instanceName;
    
    /**
     * Obtener notificaciones de un paquete
     */
    @GetMapping("/package/{packageId}")
    public ResponseEntity<?> getNotificationsByPackageId(@PathVariable String packageId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByPackageId(packageId);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", notifications);
            result.put("count", notifications.size());
            result.put("instance", instanceName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al obtener notificaciones");
            error.put("message", e.getMessage());
            error.put("instance", instanceName);
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * Obtener notificaciones por número de rastreo
     */
    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<?> getNotificationsByTrackingNumber(@PathVariable String trackingNumber) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByTrackingNumber(trackingNumber);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", notifications);
            result.put("count", notifications.size());
            result.put("trackingNumber", trackingNumber);
            result.put("instance", instanceName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al obtener notificaciones");
            error.put("message", e.getMessage());
            error.put("instance", instanceName);
            return ResponseEntity.status(500).body(error);
        }
    }
    
    /**
     * Obtener notificaciones pendientes
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingNotifications() {
        try {
            List<Notification> notifications = notificationService.getPendingNotifications();
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", notifications);
            result.put("count", notifications.size());
            result.put("instance", instanceName);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Error al obtener notificaciones pendientes");
            error.put("message", e.getMessage());
            error.put("instance", instanceName);
            return ResponseEntity.status(500).body(error);
        }
    }
}

