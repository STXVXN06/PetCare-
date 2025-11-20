package com.stxvxn.app.util;

import com.stxvxn.app.model.PackageStatus;
import java.util.List;

/**
 * Constantes del sistema centralizadas.
 * Evita valores mágicos en el código.
 */
public class Constants {
    
    // Tracking Number
    public static final String TRACKING_PREFIX = "TRK-";
    public static final int TRACKING_NUMBER_LENGTH = 8;
    
    // Employee ID
    public static final String EMPLOYEE_PREFIX = "EMP";
    public static final int EMPLOYEE_ID_LENGTH = 3;
    
    // Paginación
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    
    // Cache
    public static final String CACHE_PACKAGE = "package";
    public static final String CACHE_EMPLOYEE = "employee";
    public static final int CACHE_TTL_SECONDS = 300; // 5 minutos
    
    // Estados finales (no se pueden modificar)
    public static final List<PackageStatus> FINAL_STATUSES = List.of(
        PackageStatus.ENTREGADO,
        PackageStatus.DEVUELTO,
        PackageStatus.PERDIDO
    );
    
    private Constants() {
        // Prevenir instanciación
    }
}

