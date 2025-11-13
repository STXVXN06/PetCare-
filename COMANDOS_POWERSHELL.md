# 🔧 Comandos PowerShell para Probar la API

## Configuración Inicial

```powershell
# Configurar variables
$baseUrl = "http://localhost:8080"
$headers = @{
    "Content-Type" = "application/json"
}
```

---

## 1. Health Check

```powershell
Invoke-RestMethod -Uri "$baseUrl/ping" -Method Get | ConvertTo-Json
```

---

## 2. Crear un Nuevo Paquete

```powershell
$body = @{
    recipientName = "Juan Pérez"
    recipientAddress = "Calle 123 #45-67, Bogotá"
    recipientPhone = "+57 300 1234567"
    senderName = "María García"
    senderAddress = "Avenida Principal 789, Medellín"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "$baseUrl/api/packages" -Method Post -Headers $headers -Body $body
$trackingNumber = $response.data.trackingNumber
Write-Host "Tracking Number: $trackingNumber"
$response | ConvertTo-Json -Depth 5
```

**Guardar el tracking number:**
```powershell
$trackingNumber = "TRK-A1B2C3D4"  # Reemplaza con el número real
```

---

## 3. Obtener Paquete por Tracking Number

```powershell
$trackingNumber = "TRK-A1B2C3D4"  # Reemplaza con tu tracking number
Invoke-RestMethod -Uri "$baseUrl/api/tracking/$trackingNumber" -Method Get | ConvertTo-Json -Depth 5
```

---

## 4. Obtener Todos los Paquetes

```powershell
Invoke-RestMethod -Uri "$baseUrl/api/packages" -Method Get | ConvertTo-Json -Depth 3
```

---

## 5. Obtener Paquetes por Estado

```powershell
# Estados válidos: REGISTRADO, EN_ALMACEN, EN_TRANSITO, EN_DISTRIBUCION, EN_REPARTO, ENTREGADO, DEVUELTO, PERDIDO
Invoke-RestMethod -Uri "$baseUrl/api/packages/status/REGISTRADO" -Method Get | ConvertTo-Json -Depth 3
```

---

## 6. Obtener Paquetes por Teléfono del Destinatario

```powershell
$phone = "+57 300 1234567"
Invoke-RestMethod -Uri "$baseUrl/api/packages/recipient/$phone" -Method Get | ConvertTo-Json -Depth 3
```

---

## 7. Actualizar Estado del Paquete (Empleado)

```powershell
$trackingNumber = "TRK-A1B2C3D4"  # Reemplaza con tu tracking number

$body = @{
    status = "EN_ALMACEN"
    updatedBy = "Empleado001"
    notes = "Paquete recibido en almacén principal"
    location = "Almacén de origen"
} | ConvertTo-Json

Invoke-RestMethod -Uri "$baseUrl/api/employee/packages/$trackingNumber/status" -Method Put -Headers $headers -Body $body | ConvertTo-Json -Depth 5
```

**Ejemplo: Cambiar a EN_TRANSITO**
```powershell
$body = @{
    status = "EN_TRANSITO"
    updatedBy = "Empleado002"
    notes = "Paquete salió hacia centro de distribución"
    location = "Centro de distribución principal"
} | ConvertTo-Json

Invoke-RestMethod -Uri "$baseUrl/api/employee/packages/$trackingNumber/status" -Method Put -Headers $headers -Body $body | ConvertTo-Json -Depth 5
```

**Ejemplo: Marcar como ENTREGADO**
```powershell
$body = @{
    status = "ENTREGADO"
    updatedBy = "Repartidor001"
    notes = "Paquete entregado al destinatario"
    location = "Entregado al destinatario"
} | ConvertTo-Json

Invoke-RestMethod -Uri "$baseUrl/api/employee/packages/$trackingNumber/status" -Method Put -Headers $headers -Body $body | ConvertTo-Json -Depth 5
```

---

## 8. Obtener Notificaciones de un Paquete

```powershell
$trackingNumber = "TRK-A1B2C3D4"  # Reemplaza con tu tracking number
Invoke-RestMethod -Uri "$baseUrl/api/notifications/tracking/$trackingNumber" -Method Get | ConvertTo-Json -Depth 4
```

---

## 9. Obtener Notificaciones Pendientes

```powershell
Invoke-RestMethod -Uri "$baseUrl/api/notifications/pending" -Method Get | ConvertTo-Json -Depth 4
```

---

## 10. Iniciar Simulación de Progreso Automático

```powershell
$trackingNumber = "TRK-A1B2C3D4"  # Reemplaza con tu tracking number
Invoke-RestMethod -Uri "$baseUrl/api/tracking/$trackingNumber/simulate" -Method Post | ConvertTo-Json

# Esperar un momento y verificar el estado
Start-Sleep -Seconds 10
Invoke-RestMethod -Uri "$baseUrl/api/tracking/$trackingNumber" -Method Get | ConvertTo-Json -Depth 3
```

---

## 11. Obtener Paquetes en Tránsito

```powershell
Invoke-RestMethod -Uri "$baseUrl/api/tracking/in-transit" -Method Get | ConvertTo-Json -Depth 3
```

---

## 12. Flujo Completo: Crear, Actualizar y Rastrear

```powershell
# 1. Crear paquete
$body = @{
    recipientName = "Carlos Rodríguez"
    recipientAddress = "Carrera 15 #30-45"
    recipientPhone = "+57 310 9876543"
    senderName = "Ana Martínez"
    senderAddress = "Calle 50 #10-20"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "$baseUrl/api/packages" -Method Post -Headers $headers -Body $body
$trackingNumber = $response.data.trackingNumber
Write-Host "✅ Paquete creado: $trackingNumber" -ForegroundColor Green

# 2. Ver estado inicial
Write-Host "`n📦 Estado inicial:" -ForegroundColor Cyan
Invoke-RestMethod -Uri "$baseUrl/api/tracking/$trackingNumber" -Method Get | ConvertTo-Json -Depth 3

# 3. Actualizar a EN_ALMACEN
Write-Host "`n📦 Actualizando a EN_ALMACEN..." -ForegroundColor Cyan
$updateBody = @{
    status = "EN_ALMACEN"
    updatedBy = "Empleado001"
    notes = "Paquete recibido"
} | ConvertTo-Json
Invoke-RestMethod -Uri "$baseUrl/api/employee/packages/$trackingNumber/status" -Method Put -Headers $headers -Body $updateBody | Out-Null

# 4. Ver estado actualizado
Start-Sleep -Seconds 2
Write-Host "`n📦 Estado actualizado:" -ForegroundColor Cyan
Invoke-RestMethod -Uri "$baseUrl/api/tracking/$trackingNumber" -Method Get | ConvertTo-Json -Depth 3

# 5. Ver notificaciones
Write-Host "`n📧 Notificaciones:" -ForegroundColor Cyan
Invoke-RestMethod -Uri "$baseUrl/api/notifications/tracking/$trackingNumber" -Method Get | ConvertTo-Json -Depth 3
```

---

## 13. Monitorear Cambios de Estado (Polling)

```powershell
$trackingNumber = "TRK-A1B2C3D4"  # Reemplaza con tu tracking number
$iterations = 10

for ($i = 1; $i -le $iterations; $i++) {
    Write-Host "`n🔄 Iteración $i de $iterations" -ForegroundColor Yellow
    $response = Invoke-RestMethod -Uri "$baseUrl/api/tracking/$trackingNumber" -Method Get
    Write-Host "Estado: $($response.data.status)" -ForegroundColor Cyan
    Write-Host "Ubicación: $($response.data.currentLocation)" -ForegroundColor Cyan
    Write-Host "Actualizado: $($response.data.updatedAt)" -ForegroundColor Gray
    
    if ($response.data.status -eq "ENTREGADO" -or 
        $response.data.status -eq "DEVUELTO" -or 
        $response.data.status -eq "PERDIDO") {
        Write-Host "✅ Estado final alcanzado" -ForegroundColor Green
        break
    }
    
    Start-Sleep -Seconds 5
}
```

---

## 14. Crear Múltiples Paquetes

```powershell
$recipients = @(
    @{ name = "Juan Pérez"; phone = "+57 300 1111111"; address = "Calle 1" },
    @{ name = "María López"; phone = "+57 300 2222222"; address = "Calle 2" },
    @{ name = "Carlos Gómez"; phone = "+57 300 3333333"; address = "Calle 3" }
)

$trackingNumbers = @()

foreach ($recipient in $recipients) {
    $body = @{
        recipientName = $recipient.name
        recipientAddress = $recipient.address
        recipientPhone = $recipient.phone
        senderName = "Empresa XYZ"
        senderAddress = "Oficina Central"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/packages" -Method Post -Headers $headers -Body $body
    $trackingNumbers += $response.data.trackingNumber
    Write-Host "✅ Creado: $($response.data.trackingNumber) para $($recipient.name)" -ForegroundColor Green
}

Write-Host "`n📦 Tracking Numbers creados:" -ForegroundColor Cyan
$trackingNumbers | ForEach-Object { Write-Host "  - $_" }
```

---

## 15. Verificar Errores (Manejo de Excepciones)

```powershell
$trackingNumber = "TRK-INVALIDO"

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/tracking/$trackingNumber" -Method Get
    $response | ConvertTo-Json
} catch {
    Write-Host "❌ Error encontrado:" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
    if ($_.ErrorDetails.Message) {
        $errorJson = $_.ErrorDetails.Message | ConvertFrom-Json
        Write-Host "Mensaje: $($errorJson.error)" -ForegroundColor Red
    }
}
```

---

## 16. Usar el Script Automático

Para ejecutar todas las pruebas automáticamente:

```powershell
.\PROBAR_API.ps1
```

---

## 💡 Tips

1. **Ver respuestas formateadas:**
   ```powershell
   $response | ConvertTo-Json -Depth 10
   ```

2. **Guardar respuestas en variables:**
   ```powershell
   $package = Invoke-RestMethod -Uri "$baseUrl/api/tracking/TRK-XXX" -Method Get
   $package.data.status
   ```

3. **Filtrar datos específicos:**
   ```powershell
   $response = Invoke-RestMethod -Uri "$baseUrl/api/packages" -Method Get
   $response.data | Where-Object { $_.status -eq "EN_TRANSITO" } | ConvertTo-Json
   ```

4. **Ver solo campos específicos:**
   ```powershell
   $response = Invoke-RestMethod -Uri "$baseUrl/api/tracking/TRK-XXX" -Method Get
   $response.data | Select-Object trackingNumber, status, currentLocation | ConvertTo-Json
   ```

---

## ⚠️ Nota sobre SSE (Server-Sent Events)

El endpoint de SSE (`/api/tracking/{trackingNumber}/stream`) no se puede probar directamente con `Invoke-RestMethod` porque requiere una conexión persistente. Para probarlo:

1. **Usar un navegador:** Abre `http://localhost:8080/api/tracking/TRK-XXX/stream` en el navegador
2. **Usar herramientas especializadas:** Postman, Insomnia, o herramientas de línea de comandos como `curl` (si está disponible en Windows)

---

## 🔍 Verificar que el Servicio Esté Corriendo

```powershell
# Verificar servicios Docker
docker service ls

# Ver logs del servicio
docker service logs -f petcare_app

# Verificar que el puerto esté abierto
Test-NetConnection -ComputerName localhost -Port 8080
```

