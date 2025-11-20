package com.stxvxn.app.exception;

/**
 * Excepción lanzada cuando un paquete no se encuentra en el sistema.
 */
public class PackageNotFoundException extends BusinessException {
    
    public PackageNotFoundException(String trackingNumber) {
        super(
            "Paquete no encontrado con número de rastreo: " + trackingNumber,
            "PACKAGE_NOT_FOUND"
        );
    }
    
    public PackageNotFoundException(String id, boolean byId) {
        super(
            "Paquete no encontrado con ID: " + id,
            "PACKAGE_NOT_FOUND"
        );
    }
}

