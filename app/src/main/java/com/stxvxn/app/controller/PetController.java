package com.stxvxn.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stxvxn.app.model.Pet;
import com.stxvxn.app.repository.PetRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Value("${instance.name}")
    private String instanceName;

    @Value("${DB_HOST:mongo1,mongo2,mongo3}")
    private String dbHost;

    /**
     * Endpoint para verificar que el servicio está activo
     */
    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("instance", instanceName);
        response.put("pid", ProcessHandle.current().pid());
        response.put("db", dbHost);

        // Verificar conexión a MongoDB
        try {
            long count = petRepository.count();
            response.put("dbConnected", true);
            response.put("petsCount", count);
        } catch (Exception e) {
            response.put("dbConnected", false);
            response.put("dbError", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para crear una mascota
     */
    @PostMapping("/pets")
    public ResponseEntity<Map<String, Object>> createPet(@RequestBody Pet petRequest) {
        try {
            // Crear nueva mascota
            Pet pet = new Pet(petRequest.getName(), petRequest.getSpecies());

            // Guardar en MongoDB
            Pet savedPet = petRepository.save(pet);

            System.out.println("[" + instanceName + "] Pet creado: " + savedPet.getId());

            // Preparar respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedPet.getId());
            response.put("name", savedPet.getName());
            response.put("species", savedPet.getSpecies());
            response.put("createdAt", savedPet.getCreatedAt());
            response.put("instance", instanceName);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al crear mascota");
            error.put("message", e.getMessage());
            error.put("instance", instanceName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint para obtener una mascota por ID
     */
    @GetMapping("/pets/{id}")
    public ResponseEntity<?> getPet(@PathVariable String id) {
        try {
            // Validar formato de ID de MongoDB
            if (id == null || id.trim().isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "ID no puede estar vacío");
                error.put("instance", instanceName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Validar que el ID tenga 24 caracteres hexadecimales (formato de MongoDB
            // ObjectId)
            if (!id.matches("[0-9a-fA-F]{24}")) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Formato de ID inválido");
                error.put("message", "El ID debe ser un ObjectId válido de MongoDB (24 caracteres hexadecimales)");
                error.put("received", id);
                error.put("instance", instanceName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            Optional<Pet> petOptional = petRepository.findById(id);

            if (petOptional.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Not found");
                error.put("id", id);
                error.put("instance", instanceName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            Pet pet = petOptional.get();
            Map<String, Object> response = new HashMap<>();
            response.put("id", pet.getId());
            response.put("name", pet.getName());
            response.put("species", pet.getSpecies());
            response.put("createdAt", pet.getCreatedAt() != null ? pet.getCreatedAt().toString() : null);
            response.put("instance", instanceName);
            response.put("retrievedFrom", "MongoDB shared database");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid ID format");
            error.put("message", "El ID proporcionado no es un ObjectId válido de MongoDB");
            error.put("received", id);
            error.put("instance", instanceName);
            System.err.println("Error de formato de ID: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al buscar mascota");
            error.put("message", e.getMessage());
            error.put("instance", instanceName);
            System.err.println("Error en getPet: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint para obtener todas las mascotas
     */
    @GetMapping("/pets")
    public ResponseEntity<?> getAllPets() {
        try {
            List<Pet> pets = petRepository.findAll();

            // Convertir a un formato serializable
            List<Map<String, Object>> petsResponse = pets.stream()
                    .map(pet -> {
                        Map<String, Object> petMap = new HashMap<>();
                        petMap.put("id", pet.getId());
                        petMap.put("name", pet.getName());
                        petMap.put("species", pet.getSpecies());
                        petMap.put("createdAt", pet.getCreatedAt() != null ? pet.getCreatedAt().toString() : null);
                        return petMap;
                    })
                    .collect(java.util.stream.Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("pets", petsResponse);
            response.put("count", pets.size());
            response.put("instance", instanceName);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener mascotas");
            error.put("message", e.getMessage());
            error.put("instance", instanceName);
            System.err.println("Error en getAllPets: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Endpoint para identificar la instancia
     */
    @GetMapping("/whoami")
    public ResponseEntity<Map<String, Object>> whoami() {
        Map<String, Object> response = new HashMap<>();
        response.put("instance", instanceName);
        response.put("pid", ProcessHandle.current().pid());
        response.put("now", LocalDateTime.now());
        response.put("database", dbHost);
        return ResponseEntity.ok(response);
    }
}