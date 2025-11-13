# 📦 API de Sistema de Rastreo de Paquetes

## Descripción General

Sistema distribuido para gestión y rastreo de paquetes en tiempo real. Permite a los usuarios rastrear sus paquetes, recibir notificaciones de cambios de estado y a los empleados actualizar el estado de los paquetes.

## Base URL

```
http://localhost:8080
```

## Endpoints

### 1. Gestión de Paquetes (`/api/packages`)

#### Crear un nuevo paquete
```http
POST /api/packages
Content-Type: application/json

{
  "recipientName": "Juan Pérez",
  "recipientAddress": "Calle 123 #45-67",
  "recipientPhone": "+57 300 1234567",
  "senderName": "María García",
  "senderAddress": "Avenida Principal 789"
}
```

**Respuesta exitosa (201):**
```json
{
  "success": true,
  "data": {
    "id": "507f1f77bcf86cd799439011",
    "trackingNumber": "TRK-A1B2C3D4",
    "recipientName": "Juan Pérez",
    "status": "REGISTRADO",
    "currentLocation": "Almacén de origen",
    "createdAt": "2025-01-15T10:30:00",
    "estimatedDelivery": "2025-01-18T10:30:00"
  },
  "instance": "app"
}
```

#### Obtener paquete por ID
```http
GET /api/packages/{id}
```

#### Obtener paquete por número de rastreo
```http
GET /api/packages/tracking/{trackingNumber}
```

#### Obtener todos los paquetes
```http
GET /api/packages
```

#### Obtener paquetes por estado
```http
GET /api/packages/status/{status}
```

Estados válidos: `REGISTRADO`, `EN_ALMACEN`, `EN_TRANSITO`, `EN_DISTRIBUCION`, `EN_REPARTO`, `ENTREGADO`, `DEVUELTO`, `PERDIDO`

#### Obtener paquetes por teléfono del destinatario
```http
GET /api/packages/recipient/{phone}
```

---

### 2. Rastreo en Tiempo Real (`/api/tracking`)

#### Obtener información de rastreo
```http
GET /api/tracking/{trackingNumber}
```

**Respuesta:**
```json
{
  "success": true,
  "data": {
    "trackingNumber": "TRK-A1B2C3D4",
    "status": "EN_TRANSITO",
    "currentLocation": "Centro de distribución principal",
    "statusHistory": [
      {
        "status": "REGISTRADO",
        "timestamp": "2025-01-15T10:30:00",
        "updatedBy": "Sistema",
        "notes": ""
      },
      {
        "status": "EN_ALMACEN",
        "timestamp": "2025-01-15T11:00:00",
        "updatedBy": "Empleado001",
        "notes": "Paquete recibido en almacén"
      }
    ]
  }
}
```

#### Iniciar simulación de progreso automático
```http
POST /api/tracking/{trackingNumber}/simulate
```

Este endpoint inicia una simulación automática que actualiza el estado del paquete progresivamente.

#### Obtener paquetes en tránsito
```http
GET /api/tracking/in-transit
```

#### Rastreo en tiempo real con SSE (Server-Sent Events)
```http
GET /api/tracking/{trackingNumber}/stream
Accept: text/event-stream
```

Este endpoint permite recibir actualizaciones en tiempo real del estado del paquete mediante Server-Sent Events.

**Ejemplo de uso en JavaScript:**
```javascript
const eventSource = new EventSource('http://localhost:8080/api/tracking/TRK-A1B2C3D4/stream');

eventSource.addEventListener('update', (event) => {
  const data = JSON.parse(event.data);
  console.log('Estado actualizado:', data.status);
  console.log('Ubicación:', data.currentLocation);
});
```

---

### 3. Gestión de Empleados (`/api/employee`)

#### Actualizar estado de un paquete
```http
PUT /api/employee/packages/{trackingNumber}/status
Content-Type: application/json

{
  "status": "EN_REPARTO",
  "updatedBy": "Empleado001",
  "notes": "Paquete salió para entrega",
  "location": "En ruta de entrega"
}
```

**Estados válidos:**
- `REGISTRADO` → `EN_ALMACEN` o `EN_TRANSITO`
- `EN_ALMACEN` → `EN_TRANSITO` o `DEVUELTO`
- `EN_TRANSITO` → `EN_DISTRIBUCION`, `EN_ALMACEN` o `PERDIDO`
- `EN_DISTRIBUCION` → `EN_REPARTO` o `EN_TRANSITO`
- `EN_REPARTO` → `ENTREGADO`, `EN_DISTRIBUCION` o `DEVUELTO`

**Respuesta exitosa:**
```json
{
  "success": true,
  "message": "Estado actualizado exitosamente",
  "data": {
    "trackingNumber": "TRK-A1B2C3D4",
    "status": "EN_REPARTO",
    "currentLocation": "En ruta de entrega",
    "updatedAt": "2025-01-15T14:30:00"
  }
}
```

---

### 4. Notificaciones (`/api/notifications`)

#### Obtener notificaciones de un paquete
```http
GET /api/notifications/package/{packageId}
```

#### Obtener notificaciones por número de rastreo
```http
GET /api/notifications/tracking/{trackingNumber}
```

#### Obtener notificaciones pendientes
```http
GET /api/notifications/pending
```

**Respuesta:**
```json
{
  "success": true,
  "data": [
    {
      "id": "507f1f77bcf86cd799439012",
      "packageId": "507f1f77bcf86cd799439011",
      "trackingNumber": "TRK-A1B2C3D4",
      "message": "Su paquete con número de rastreo TRK-A1B2C3D4 está en tránsito hacia su destino.",
      "type": "STATUS_UPDATE",
      "status": "SENT",
      "createdAt": "2025-01-15T11:00:00"
    }
  ],
  "count": 1
}
```

---

### 5. Endpoints de Sistema

#### Health Check
```http
GET /ping
```

#### Identificar instancia
```http
GET /whoami
```

---

## Flujo de Uso Típico

### 1. Cliente crea un paquete
```bash
curl -X POST http://localhost:8080/api/packages \
  -H "Content-Type: application/json" \
  -d '{
    "recipientName": "Juan Pérez",
    "recipientAddress": "Calle 123 #45-67",
    "recipientPhone": "+57 300 1234567",
    "senderName": "María García",
    "senderAddress": "Avenida Principal 789"
  }'
```

### 2. Cliente rastrea su paquete
```bash
curl http://localhost:8080/api/tracking/TRK-A1B2C3D4
```

### 3. Empleado actualiza el estado
```bash
curl -X PUT http://localhost:8080/api/employee/packages/TRK-A1B2C3D4/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "EN_REPARTO",
    "updatedBy": "Empleado001",
    "notes": "Paquete salió para entrega"
  }'
```

### 4. Cliente consulta notificaciones
```bash
curl http://localhost:8080/api/notifications/tracking/TRK-A1B2C3D4
```

---

## Estados del Paquete

| Estado | Descripción |
|--------|-------------|
| `REGISTRADO` | El paquete ha sido registrado en el sistema |
| `EN_ALMACEN` | El paquete está en el almacén de origen |
| `EN_TRANSITO` | El paquete está en camino al destino |
| `EN_DISTRIBUCION` | El paquete está siendo distribuido localmente |
| `EN_REPARTO` | El paquete está siendo entregado |
| `ENTREGADO` | El paquete ha sido entregado exitosamente |
| `DEVUELTO` | El paquete ha sido devuelto al remitente |
| `PERDIDO` | El paquete se ha reportado como perdido |

---

## Notas Importantes

1. **Números de rastreo**: Se generan automáticamente con formato `TRK-XXXXXXXX` (8 caracteres alfanuméricos).

2. **Notificaciones automáticas**: Cada cambio de estado genera automáticamente una notificación que se envía al destinatario.

3. **Validación de transiciones**: El sistema valida que las transiciones de estado sean lógicas. No se puede saltar estados o retroceder de forma inválida.

4. **Rastreo simulado**: El sistema incluye una función de simulación automática que actualiza los estados progresivamente para demostración.

5. **Tiempo real**: Se puede usar el endpoint SSE (`/api/tracking/{trackingNumber}/stream`) para recibir actualizaciones en tiempo real sin necesidad de hacer polling.

---

## Ejemplos de Uso Completo

### Crear y rastrear un paquete completo

```bash
# 1. Crear paquete
RESPONSE=$(curl -s -X POST http://localhost:8080/api/packages \
  -H "Content-Type: application/json" \
  -d '{
    "recipientName": "Juan Pérez",
    "recipientAddress": "Calle 123 #45-67",
    "recipientPhone": "+57 300 1234567",
    "senderName": "María García",
    "senderAddress": "Avenida Principal 789"
  }')

# Extraer tracking number
TRACKING=$(echo $RESPONSE | jq -r '.data.trackingNumber')
echo "Tracking Number: $TRACKING"

# 2. Rastrear paquete
curl http://localhost:8080/api/tracking/$TRACKING

# 3. Iniciar simulación automática
curl -X POST http://localhost:8080/api/tracking/$TRACKING/simulate

# 4. Ver notificaciones
curl http://localhost:8080/api/notifications/tracking/$TRACKING
```

