# Peticiones para Probar la API PetCare desde Postman

**URL Base:** `http://localhost:8888`

---

## 1. Health Check y Utilidades

### 1.1 Ping - Verificar estado del servicio
```
GET http://localhost:8888/ping
```

### 1.2 Whoami - Identificar instancia
```
GET http://localhost:8888/whoami
```

---

## 2. Gestión de Mascotas (Pets)

### 2.1 Crear una mascota
```
POST http://localhost:8888/pets
Content-Type: application/json

{
  "name": "Max",
  "species": "Perro"
}
```

### 2.2 Obtener todas las mascotas
```
GET http://localhost:8888/pets
```

### 2.3 Obtener una mascota por ID
```
GET http://localhost:8888/pets/{id}
```
**Ejemplo:**
```
GET http://localhost:8888/pets/507f1f77bcf86cd799439011
```

---

## 3. Gestión de Paquetes (Packages)

### 3.1 Crear un nuevo paquete
```
POST http://localhost:8888/api/packages
Content-Type: application/json

{
  "recipientName": "Juan Pérez",
  "recipientAddress": "Calle 123 #45-67, Bogotá",
  "recipientPhone": "+573001234567",
  "senderName": "María García",
  "senderAddress": "Avenida 456 #12-34, Medellín"
}
```

### 3.2 Obtener todos los paquetes
```
GET http://localhost:8888/api/packages
```

### 3.3 Obtener todos los paquetes con paginación
```
GET http://localhost:8888/api/packages?page=0&size=10&sort=createdAt&direction=DESC
```

### 3.4 Obtener un paquete por ID
```
GET http://localhost:8888/api/packages/{id}
```
**Ejemplo:**
```
GET http://localhost:8888/api/packages/507f1f77bcf86cd799439011
```

### 3.5 Obtener un paquete por número de rastreo
```
GET http://localhost:8888/api/packages/tracking/{trackingNumber}
```
**Ejemplo:**
```
GET http://localhost:8888/api/packages/tracking/TRK123456789
```

### 3.6 Obtener paquetes por estado
```
GET http://localhost:8888/api/packages/status/{status}
```
**Estados disponibles:**
- `REGISTRADO`
- `EN_ALMACEN`
- `EN_TRANSITO`
- `EN_DISTRIBUCION`
- `EN_REPARTO`
- `ENTREGADO`
- `DEVUELTO`
- `PERDIDO`

**Ejemplo:**
```
GET http://localhost:8888/api/packages/status/EN_TRANSITO
```

### 3.7 Obtener paquetes por estado con paginación
```
GET http://localhost:8888/api/packages/status/EN_TRANSITO?page=0&size=5&sort=updatedAt&direction=DESC
```

### 3.8 Obtener paquetes por teléfono del destinatario
```
GET http://localhost:8888/api/packages/recipient/{phone}
```
**Ejemplo:**
```
GET http://localhost:8888/api/packages/recipient/+573001234567
```

---

## 4. Rastreo de Paquetes (Tracking)

### 4.1 Obtener información de rastreo
```
GET http://localhost:8888/api/tracking/{trackingNumber}
```
**Ejemplo:**
```
GET http://localhost:8888/api/tracking/TRK123456789
```

### 4.2 Iniciar simulación de progreso automático
```
POST http://localhost:8888/api/tracking/{trackingNumber}/simulate
```
**Ejemplo:**
```
POST http://localhost:8888/api/tracking/TRK123456789/simulate
```

### 4.3 Obtener todos los paquetes en tránsito
```
GET http://localhost:8888/api/tracking/in-transit
```

### 4.4 Stream de rastreo en tiempo real (Server-Sent Events)
```
GET http://localhost:8888/api/tracking/{trackingNumber}/stream
Accept: text/event-stream
```
**Nota:** Este endpoint requiere un cliente que soporte SSE. En Postman, puedes probarlo pero no verás el stream completo.

---

## 5. Gestión de Empleados (Employees)

### 5.1 Crear un nuevo empleado
```
POST http://localhost:8888/api/employees
Content-Type: application/json

{
  "name": "Carlos Rodríguez",
  "email": "carlos.rodriguez@petcare.com",
  "role": "OPERATOR",
  "department": "Logística"
}
```

**Roles disponibles:**
- `ADMIN` - Administrador
- `MANAGER` - Gerente
- `OPERATOR` - Operador
- `DELIVERY` - Repartidor

**Ejemplo con diferentes roles:**
```json
// Administrador
{
  "name": "Ana López",
  "email": "ana.lopez@petcare.com",
  "role": "ADMIN",
  "department": "Administración"
}

// Repartidor
{
  "name": "Pedro Martínez",
  "email": "pedro.martinez@petcare.com",
  "role": "DELIVERY",
  "department": "Distribución"
}
```

### 5.2 Obtener todos los empleados
```
GET http://localhost:8888/api/employees
```

### 5.3 Obtener un empleado por ID
```
GET http://localhost:8888/api/employees/{id}
```
**Ejemplo:**
```
GET http://localhost:8888/api/employees/507f1f77bcf86cd799439011
```

### 5.4 Obtener un empleado por employeeId
```
GET http://localhost:8888/api/employees/employee-id/{employeeId}
```
**Ejemplo:**
```
GET http://localhost:8888/api/employees/employee-id/EMP001
```

### 5.5 Obtener empleados por rol
```
GET http://localhost:8888/api/employees/role/{role}
```
**Ejemplo:**
```
GET http://localhost:8888/api/employees/role/OPERATOR
```

### 5.6 Actualizar estado de un paquete (por empleado)
```
PUT http://localhost:8888/api/employees/packages/{trackingNumber}/status
Content-Type: application/json

{
  "status": "EN_TRANSITO",
  "updatedBy": "EMP001",
  "notes": "Paquete salió del almacén principal",
  "location": "Bogotá - Terminal de Carga"
}
```

**Estados válidos para actualización:**
- `REGISTRADO`
- `EN_ALMACEN`
- `EN_TRANSITO`
- `EN_DISTRIBUCION`
- `EN_REPARTO`
- `ENTREGADO`
- `DEVUELTO`
- `PERDIDO`

**Ejemplo de transición completa:**
```json
// 1. De REGISTRADO a EN_ALMACEN
{
  "status": "EN_ALMACEN",
  "updatedBy": "EMP001",
  "notes": "Paquete recibido en almacén",
  "location": "Almacén Central - Bogotá"
}

// 2. De EN_ALMACEN a EN_TRANSITO
{
  "status": "EN_TRANSITO",
  "updatedBy": "EMP002",
  "notes": "Enviado a ciudad destino",
  "location": "En ruta a Medellín"
}

// 3. De EN_TRANSITO a EN_DISTRIBUCION
{
  "status": "EN_DISTRIBUCION",
  "updatedBy": "EMP003",
  "notes": "Llegó a centro de distribución",
  "location": "Centro de Distribución - Medellín"
}

// 4. De EN_DISTRIBUCION a EN_REPARTO
{
  "status": "EN_REPARTO",
  "updatedBy": "EMP004",
  "notes": "Asignado a repartidor",
  "location": "En ruta de entrega"
}

// 5. De EN_REPARTO a ENTREGADO
{
  "status": "ENTREGADO",
  "updatedBy": "EMP004",
  "notes": "Entregado exitosamente al destinatario",
  "location": "Calle 123 #45-67, Bogotá"
}
```

---

## 6. Flujo Completo de Ejemplo

### Paso 1: Crear un empleado
```
POST http://localhost:8888/api/employees
Content-Type: application/json

{
  "name": "Juan Operador",
  "email": "juan.operador@petcare.com",
  "role": "OPERATOR",
  "department": "Operaciones"
}
```
**Guardar el `employeeId` de la respuesta (ej: `EMP001`)**

### Paso 2: Crear un paquete
```
POST http://localhost:8888/api/packages
Content-Type: application/json

{
  "recipientName": "María González",
  "recipientAddress": "Carrera 15 #20-30, Cali",
  "recipientPhone": "+573001111111",
  "senderName": "Pedro Sánchez",
  "senderAddress": "Avenida 68 #25-10, Bogotá"
}
```
**Guardar el `trackingNumber` de la respuesta (ej: `TRK123456789`)**

### Paso 3: Obtener información del paquete
```
GET http://localhost:8888/api/packages/tracking/TRK123456789
```

### Paso 4: Actualizar estado del paquete
```
PUT http://localhost:8888/api/employees/packages/TRK123456789/status
Content-Type: application/json

{
  "status": "EN_ALMACEN",
  "updatedBy": "EMP001",
  "notes": "Paquete recibido",
  "location": "Almacén Central"
}
```

### Paso 5: Rastrear el paquete
```
GET http://localhost:8888/api/tracking/TRK123456789
```

### Paso 6: Iniciar simulación de progreso
```
POST http://localhost:8888/api/tracking/TRK123456789/simulate
```

### Paso 7: Ver paquetes en tránsito
```
GET http://localhost:8888/api/tracking/in-transit
```

---

## 7. Headers Recomendados

Para todas las peticiones POST y PUT, incluir:
```
Content-Type: application/json
```

Para peticiones GET, no se requieren headers especiales.

---

## 8. Códigos de Respuesta Esperados

- `200 OK` - Operación exitosa
- `201 Created` - Recurso creado exitosamente
- `400 Bad Request` - Error de validación o datos inválidos
- `404 Not Found` - Recurso no encontrado
- `500 Internal Server Error` - Error del servidor

---

## 9. Notas Importantes

1. **IDs de MongoDB**: Los IDs deben tener 24 caracteres hexadecimales (formato ObjectId)
2. **Números de teléfono**: Deben tener entre 10 y 15 dígitos, pueden incluir el prefijo `+`
3. **Estados de paquetes**: Deben seguir el flujo de transiciones válidas
4. **EmployeeId**: Se genera automáticamente al crear un empleado (formato: EMP001, EMP002, etc.)
5. **TrackingNumber**: Se genera automáticamente al crear un paquete (formato: TRK seguido de números)

---

## 10. Colección de Postman

Para importar estas peticiones en Postman, puedes crear una colección con las siguientes carpetas:
- Health Check
- Pets
- Packages
- Tracking
- Employees

Cada petición debe configurarse con:
- Método HTTP correcto
- URL completa
- Headers necesarios
- Body (para POST/PUT) en formato JSON

