package com.stxvxn.app.model;

import lombok.Getter;

/**
 * Roles disponibles para empleados en el sistema.
 */
@Getter
public enum EmployeeRole {
    ADMIN("Administrador", "Acceso completo al sistema"),
    MANAGER("Gerente", "Gestión de operaciones y reportes"),
    OPERATOR("Operador", "Actualización de estados de paquetes"),
    DELIVERY("Repartidor", "Entrega de paquetes");
    
    private final String displayName;
    private final String description;
    
    EmployeeRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}

