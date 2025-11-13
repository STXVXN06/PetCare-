# Script para crear un paquete - Versión corregida
# Uso: .\CREAR_PAQUETE.ps1

$baseUrl = "http://localhost:8080"

Write-Host "📦 Creando un nuevo paquete..." -ForegroundColor Cyan
Write-Host "==============================`n" -ForegroundColor Cyan

# Verificar que el servicio esté activo primero
Write-Host "1️⃣ Verificando que el servicio esté activo..." -ForegroundColor Yellow
try {
    $pingResponse = Invoke-RestMethod -Uri "$baseUrl/ping" -Method Get -ErrorAction Stop
    Write-Host "✅ Servicio activo" -ForegroundColor Green
} catch {
    Write-Host "❌ El servicio no está respondiendo en $baseUrl" -ForegroundColor Red
    Write-Host "   Verifica que el servicio esté corriendo:" -ForegroundColor Yellow
    Write-Host "   docker service ls" -ForegroundColor Gray
    exit 1
}

# Crear el body del request
Write-Host "`n2️⃣ Preparando datos del paquete..." -ForegroundColor Yellow
$bodyObject = @{
    recipientName = "Juan Pérez"
    recipientAddress = "Calle 123 #45-67, Bogotá"
    recipientPhone = "+57 300 1234567"
    senderName = "María García"
    senderAddress = "Avenida Principal 789, Medellín"
}

# Convertir a JSON con UTF-8
$bodyJson = $bodyObject | ConvertTo-Json -Depth 10

Write-Host "Body JSON:" -ForegroundColor Gray
Write-Host $bodyJson -ForegroundColor DarkGray

# Configurar headers correctamente
$headers = @{
    "Content-Type" = "application/json; charset=utf-8"
}

# Realizar la petición
Write-Host "`n3️⃣ Enviando petición..." -ForegroundColor Yellow
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/packages" `
        -Method Post `
        -Headers $headers `
        -Body ([System.Text.Encoding]::UTF8.GetBytes($bodyJson)) `
        -ContentType "application/json; charset=utf-8" `
        -ErrorAction Stop
    
    Write-Host "✅ Paquete creado exitosamente!" -ForegroundColor Green
    Write-Host "`n📦 Información del paquete:" -ForegroundColor Cyan
    $response | ConvertTo-Json -Depth 10
    
    $trackingNumber = $response.data.trackingNumber
    Write-Host "`n🔑 Tracking Number: $trackingNumber" -ForegroundColor Yellow
    Write-Host "`n💡 Para rastrear el paquete:" -ForegroundColor Cyan
    Write-Host "   Invoke-RestMethod -Uri `"$baseUrl/api/tracking/$trackingNumber`" -Method Get | ConvertTo-Json" -ForegroundColor Gray
    
} catch {
    Write-Host "`n❌ Error al crear el paquete" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
    
    # Intentar leer el mensaje de error
    if ($_.ErrorDetails.Message) {
        Write-Host "`nMensaje de error del servidor:" -ForegroundColor Yellow
        try {
            $errorJson = $_.ErrorDetails.Message | ConvertFrom-Json
            $errorJson | ConvertTo-Json -Depth 5
        } catch {
            Write-Host $_.ErrorDetails.Message -ForegroundColor Red
        }
    } else {
        Write-Host "Mensaje: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Write-Host "`n🔍 Verificaciones:" -ForegroundColor Yellow
    Write-Host "1. ¿El servicio está corriendo? (docker service ls)" -ForegroundColor Gray
    Write-Host "2. ¿El puerto 8080 está accesible? (Test-NetConnection localhost -Port 8080)" -ForegroundColor Gray
    Write-Host "3. ¿Los campos requeridos están presentes?" -ForegroundColor Gray
}

