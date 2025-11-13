package com.stxvxn.app.model;

/**
 * Enum que representa los estados posibles de un paquete
 */
public enum PackageStatus {
    REGISTRADO("Registrado", "El paquete ha sido registrado en el sistema"),
    EN_ALMACEN("En Almacén", "El paquete está en el almacén de origen"),
    EN_TRANSITO("En Tránsito", "El paquete está en camino al destino"),
    EN_DISTRIBUCION("En Distribución", "El paquete está siendo distribuido localmente"),
    EN_REPARTO("En Reparto", "El paquete está siendo entregado"),
    ENTREGADO("Entregado", "El paquete ha sido entregado exitosamente"),
    DEVUELTO("Devuelto", "El paquete ha sido devuelto al remitente"),
    PERDIDO("Perdido", "El paquete se ha reportado como perdido");

    private final String displayName;
    private final String description;

    PackageStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Verifica si el estado permite transición a otro estado
     */
    public boolean canTransitionTo(PackageStatus newStatus) {
        // Lógica simple de transiciones válidas
        switch (this) {
            case REGISTRADO:
                return newStatus == EN_ALMACEN || newStatus == EN_TRANSITO;
            case EN_ALMACEN:
                return newStatus == EN_TRANSITO || newStatus == DEVUELTO;
            case EN_TRANSITO:
                return newStatus == EN_DISTRIBUCION || newStatus == EN_ALMACEN || newStatus == PERDIDO;
            case EN_DISTRIBUCION:
                return newStatus == EN_REPARTO || newStatus == EN_TRANSITO;
            case EN_REPARTO:
                return newStatus == ENTREGADO || newStatus == EN_DISTRIBUCION || newStatus == DEVUELTO;
            case ENTREGADO:
                return false; // Estado final
            case DEVUELTO:
                return false; // Estado final
            case PERDIDO:
                return false; // Estado final
            default:
                return false;
        }
    }
}

