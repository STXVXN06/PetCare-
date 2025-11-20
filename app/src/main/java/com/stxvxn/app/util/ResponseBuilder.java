package com.stxvxn.app.util;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilidad para construir respuestas estandarizadas.
 * Facilita la creación de respuestas consistentes en los controladores.
 */
public class ResponseBuilder {
    
    /**
     * Construye una respuesta exitosa con datos.
     * 
     * @param <T> Tipo de datos
     * @param data Datos a incluir en la respuesta
     * @return Mapa con la estructura de respuesta
     */
    public static <T> Map<String, Object> success(T data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
    
    /**
     * Construye una respuesta exitosa con datos e instancia.
     * 
     * @param <T> Tipo de datos
     * @param data Datos a incluir
     * @param instanceName Nombre de la instancia
     * @return Mapa con la estructura de respuesta
     */
    public static <T> Map<String, Object> success(T data, String instanceName) {
        Map<String, Object> response = success(data);
        response.put("instance", instanceName);
        return response;
    }
    
    /**
     * Construye una respuesta exitosa con paginación.
     * 
     * @param content Contenido de la página
     * @param page Número de página
     * @param size Tamaño de página
     * @param total Total de elementos
     * @return Mapa con la estructura de respuesta paginada
     */
    public static Map<String, Object> successWithPagination(
            Object content, 
            int page, 
            int size, 
            long total) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", content);
        response.put("pagination", Map.of(
            "page", page,
            "size", size,
            "totalElements", total,
            "totalPages", (int) Math.ceil((double) total / size),
            "hasNext", (page + 1) * size < total,
            "hasPrevious", page > 0
        ));
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
    
    /**
     * Construye una respuesta de error.
     * 
     * @param message Mensaje de error
     * @param code Código de error
     * @return Mapa con la estructura de error
     */
    public static Map<String, Object> error(String message, String code) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        response.put("code", code);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
    
    private ResponseBuilder() {
        // Prevenir instanciación
    }
}

