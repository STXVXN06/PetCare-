package com.stxvxn.app.repository;

import com.stxvxn.app.model.PackageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Package
 */
@Repository
public interface PackageRepository extends MongoRepository<com.stxvxn.app.model.Package, String> {
    
    /**
     * Busca un paquete por su número de rastreo
     */
    Optional<com.stxvxn.app.model.Package> findByTrackingNumber(String trackingNumber);
    
    /**
     * Busca todos los paquetes con un estado específico
     */
    List<com.stxvxn.app.model.Package> findByStatus(PackageStatus status);
    
    /**
     * Busca todos los paquetes con un estado específico con paginación
     */
    Page<com.stxvxn.app.model.Package> findByStatus(PackageStatus status, Pageable pageable);
    
    /**
     * Busca paquetes por teléfono del destinatario
     */
    List<com.stxvxn.app.model.Package> findByRecipientPhone(String recipientPhone);
    
    /**
     * Busca paquetes por nombre del destinatario
     */
    List<com.stxvxn.app.model.Package> findByRecipientNameContainingIgnoreCase(String recipientName);
}

