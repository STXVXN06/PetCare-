# 📦 Sistema de Rastreo de Paquetes - Documentación Técnica

## 🎯 Descripción del Sistema

Sistema distribuido para gestión y rastreo de paquetes en tiempo real, implementado con Spring Boot, MongoDB y Docker Swarm. El sistema permite:

1. **Rastrear paquetes en tiempo real** (simulado)
2. **Notificar a clientes** sobre cambios de estado importantes
3. **Permitir a empleados** actualizar el estado de los paquetes

## 🏗️ Arquitectura

### Estructura del Proyecto

```
app/
├── src/main/java/com/stxvxn/app/
│   ├── model/              # Entidades
│   │   ├── Package.java
│   │   ├── PackageStatus.java (enum)
│   │   └── Notification.java
│   ├── repository/         # Repositorios MongoDB
│   │   ├── PackageRepository.java
│   │   └── NotificationRepository.java
│   ├── service/            # Servicios (interfaces + implementaciones)
│   │   ├── PackageService.java
│   │   ├── PackageServiceImpl.java
│   │   ├── NotificationService.java
│   │   ├── NotificationServiceImpl.java
│   │   ├── TrackingService.java
│   │   └── TrackingServiceImpl.java
│   ├── controller/         # Controladores REST
│   │   ├── PackageController.java
│   │   ├── TrackingController.java
│   │   ├── EmployeeController.java
│   │   └── NotificationController.java
│   ├── dto/                # Data Transfer Objects
│   │   ├── CreatePackageRequest.java
│   │   ├── UpdateStatusRequest.java
│   │   └── PackageResponse.java
│   └── config/             # Configuraciones
│       └── AsyncConfig.java
```

## 📋 Componentes Principales

### 1. Entidades

#### Package
- Representa un paquete en el sistema
- Campos principales:
  - `trackingNumber`: Número único de rastreo
  - `status`: Estado actual (enum PackageStatus)
  - `currentLocation`: Ubicación actual simulada
  - `statusHistory`: Historial de cambios de estado
  - `recipientName`, `recipientAddress`, `recipientPhone`
  - `senderName`, `senderAddress`

#### PackageStatus (Enum)
Estados posibles:
- `REGISTRADO` → `EN_ALMACEN` → `EN_TRANSITO` → `EN_DISTRIBUCION` → `EN_REPARTO` → `ENTREGADO`
- También puede: `DEVUELTO`, `PERDIDO`

#### Notification
- Representa notificaciones enviadas a clientes
- Tipos: `STATUS_UPDATE`, `DELIVERY_CONFIRMED`, `DELAY_ALERT`, `EXCEPTION`
- Estados: `PENDING`, `SENT`, `FAILED`, `DELIVERED`

### 2. Servicios

#### PackageService
- **Interfaz**: Define operaciones CRUD y gestión de paquetes
- **Implementación**: `PackageServiceImpl`
  - Crea paquetes con número de rastreo único
  - Valida transiciones de estado
  - Genera notificaciones automáticas

#### NotificationService
- **Interfaz**: Define operaciones de notificaciones
- **Implementación**: `NotificationServiceImpl`
  - Crea notificaciones automáticas al cambiar estado
  - Construye mensajes personalizados según estado
  - Simula envío (en producción sería SMS/Email/Push)

#### TrackingService
- **Interfaz**: Define operaciones de rastreo
- **Implementación**: `TrackingServiceImpl`
  - Simula progreso automático de paquetes
  - Actualiza estados con delays simulados
  - Soporta rastreo en tiempo real

### 3. Controladores

#### PackageController (`/api/packages`)
- `POST /api/packages` - Crear paquete
- `GET /api/packages/{id}` - Obtener por ID
- `GET /api/packages/tracking/{trackingNumber}` - Obtener por tracking
- `GET /api/packages` - Listar todos
- `GET /api/packages/status/{status}` - Filtrar por estado
- `GET /api/packages/recipient/{phone}` - Filtrar por teléfono

#### TrackingController (`/api/tracking`)
- `GET /api/tracking/{trackingNumber}` - Información de rastreo
- `POST /api/tracking/{trackingNumber}/simulate` - Iniciar simulación
- `GET /api/tracking/in-transit` - Paquetes en tránsito
- `GET /api/tracking/{trackingNumber}/stream` - SSE para tiempo real

#### EmployeeController (`/api/employee`)
- `PUT /api/employee/packages/{trackingNumber}/status` - Actualizar estado

#### NotificationController (`/api/notifications`)
- `GET /api/notifications/package/{packageId}` - Notificaciones de un paquete
- `GET /api/notifications/tracking/{trackingNumber}` - Por tracking
- `GET /api/notifications/pending` - Pendientes

## 🔄 Flujos Principales

### Flujo 1: Crear y Rastrear un Paquete

1. **Cliente crea paquete**
   ```
   POST /api/packages
   → Sistema genera trackingNumber único (TRK-XXXXXXXX)
   → Estado inicial: REGISTRADO
   → Se crea notificación automática
   ```

2. **Cliente rastrea paquete**
   ```
   GET /api/tracking/{trackingNumber}
   → Retorna estado actual, ubicación, historial
   ```

3. **Sistema simula progreso** (opcional)
   ```
   POST /api/tracking/{trackingNumber}/simulate
   → Actualiza estados automáticamente con delays
   → Genera notificaciones en cada cambio
   ```

### Flujo 2: Empleado Actualiza Estado

1. **Empleado actualiza estado**
   ```
   PUT /api/employee/packages/{trackingNumber}/status
   {
     "status": "EN_REPARTO",
     "updatedBy": "Empleado001",
     "notes": "Paquete salió para entrega"
   }
   ```

2. **Sistema valida transición**
   - Verifica que la transición sea válida
   - Actualiza ubicación según estado
   - Agrega entrada al historial

3. **Sistema genera notificación**
   - Crea notificación automática
   - Envía mensaje al destinatario (simulado)

### Flujo 3: Rastreo en Tiempo Real (SSE)

1. **Cliente se conecta a SSE**
   ```javascript
   const eventSource = new EventSource(
     'http://localhost:8080/api/tracking/TRK-XXX/stream'
   );
   ```

2. **Sistema envía actualizaciones**
   - Cada 3 segundos envía estado actual
   - Continúa hasta estado final o timeout

3. **Cliente recibe actualizaciones**
   ```javascript
   eventSource.addEventListener('update', (event) => {
     const data = JSON.parse(event.data);
     // Actualizar UI con nuevo estado
   });
   ```

## 🗄️ Base de Datos MongoDB

### Colecciones

#### `packages`
```json
{
  "_id": "ObjectId",
  "trackingNumber": "TRK-A1B2C3D4",
  "recipientName": "Juan Pérez",
  "recipientAddress": "Calle 123",
  "recipientPhone": "+57 300 1234567",
  "senderName": "María García",
  "senderAddress": "Avenida Principal",
  "status": "EN_TRANSITO",
  "currentLocation": "Centro de distribución",
  "statusHistory": [
    {
      "status": "REGISTRADO",
      "timestamp": "2025-01-15T10:30:00",
      "updatedBy": "Sistema",
      "notes": ""
    }
  ],
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T11:00:00",
  "estimatedDelivery": "2025-01-18T10:30:00"
}
```

#### `notifications`
```json
{
  "_id": "ObjectId",
  "packageId": "ObjectId",
  "trackingNumber": "TRK-A1B2C3D4",
  "recipientPhone": "+57 300 1234567",
  "message": "Su paquete está en tránsito...",
  "type": "STATUS_UPDATE",
  "status": "SENT",
  "createdAt": "2025-01-15T11:00:00",
  "sentAt": "2025-01-15T11:00:01"
}
```

## 🔧 Configuración

### application.properties
- Puerto: `3000` (configurable con `PORT`)
- MongoDB: Replica Set configurado
- Actuator: Habilitado para métricas Prometheus
- Async: Habilitado para procesamiento asíncrono

### AsyncConfig
- Thread pool para procesamiento asíncrono
- Core pool: 2 threads
- Max pool: 5 threads
- Queue capacity: 100

## 🚀 Despliegue

El sistema se despliega usando Docker Swarm:

```bash
# Construir imagen
docker build -t petcare-app:latest ./app

# Desplegar stack
docker stack deploy -c docker-stack.yaml petcare

# Ver servicios
docker service ls

# Ver logs
docker service logs -f petcare_app
```

## 📊 Monitoreo

- **Prometheus**: `http://localhost:9090`
- **Grafana**: `http://localhost:3001`
- **Actuator**: `http://localhost:8080/actuator`
- **Health**: `http://localhost:8080/actuator/health`

## ✅ Buenas Prácticas Implementadas

1. **Separación de responsabilidades**
   - Entidades, Repositorios, Servicios, Controladores separados

2. **Interfaces y implementaciones**
   - Todos los servicios tienen interfaces
   - Facilita testing y mantenimiento

3. **DTOs para requests/responses**
   - No se exponen entidades directamente
   - Control de datos expuestos

4. **Validación de transiciones**
   - Estados solo pueden cambiar según reglas de negocio

5. **Notificaciones automáticas**
   - Cada cambio de estado genera notificación
   - Desacoplado del flujo principal

6. **Procesamiento asíncrono**
   - Simulación de progreso no bloquea requests
   - SSE para actualizaciones en tiempo real

## 🔍 Próximas Mejoras

1. Autenticación y autorización (JWT)
2. Integración real con SMS/Email
3. WebSockets para tiempo real bidireccional
4. Cache con Redis para mejor rendimiento
5. Event sourcing para auditoría completa
6. Tests unitarios y de integración

