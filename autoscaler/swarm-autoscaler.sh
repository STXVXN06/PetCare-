#!/bin/bash

SERVICE_NAME="petcare_app"
MAX_REPLICAS=5
MIN_REPLICAS=2
CPU_THRESHOLD_HIGH=60  # ✅ Aumentado de 30% a 60%
CPU_THRESHOLD_LOW=20
CHECK_INTERVAL=30      # ✅ Aumentado de 15s a 30s
COOLDOWN_PERIOD=120    # ✅ NUEVO: Esperar 2 minutos después de escalar

LAST_SCALE_TIME=0

echo "🚀 Iniciando autoescalador para $SERVICE_NAME"
echo "📋 Configuración:"
echo "   - Réplicas: MIN=$MIN_REPLICAS, MAX=$MAX_REPLICAS"
echo "   - CPU Thresholds: HIGH=${CPU_THRESHOLD_HIGH}%, LOW=${CPU_THRESHOLD_LOW}%"
echo "   - Check Interval: ${CHECK_INTERVAL}s"
echo "   - Cooldown Period: ${COOLDOWN_PERIOD}s"
echo ""

# Verificar que bc está instalado
if ! command -v bc &> /dev/null; then
    echo "❌ ERROR: bc no está instalado"
    exit 1
fi

# Verificar acceso a Docker socket
if [ ! -S /var/run/docker.sock ]; then
    echo "❌ ERROR: No se puede acceder a Docker socket"
    exit 1
fi

# Verificar que el servicio existe
if ! docker service ls | grep -q "$SERVICE_NAME"; then
    echo "⚠️  ADVERTENCIA: El servicio $SERVICE_NAME no existe todavía"
    echo "   Esperando a que se cree..."
fi

while true; do
    # Verificar que el servicio existe
    if ! docker service ls | grep -q "$SERVICE_NAME"; then
        echo "⏳ Esperando a que el servicio $SERVICE_NAME se cree..."
        sleep $CHECK_INTERVAL
        continue
    fi

    # Obtener número de réplicas actuales
    REPLICAS_INFO=$(docker service ls --filter name=$SERVICE_NAME --format "{{.Replicas}}" 2>/dev/null)
    
    if [ -z "$REPLICAS_INFO" ]; then
        echo "⚠️  No se pudo obtener información de réplicas"
        sleep $CHECK_INTERVAL
        continue
    fi
    
    CURRENT_REPLICAS=$(echo $REPLICAS_INFO | cut -d'/' -f1)
    DESIRED_REPLICAS=$(echo $REPLICAS_INFO | cut -d'/' -f2)
    
    CURRENT_TIME=$(date +%s)
    TIME_SINCE_LAST_SCALE=$((CURRENT_TIME - LAST_SCALE_TIME))
    
    echo ""
    echo "📊 [$(date '+%Y-%m-%d %H:%M:%S')]"
    echo "   Réplicas: $CURRENT_REPLICAS/$DESIRED_REPLICAS"
    
    # Verificar si estamos en cooldown
    if [ $LAST_SCALE_TIME -gt 0 ] && [ $TIME_SINCE_LAST_SCALE -lt $COOLDOWN_PERIOD ]; then
        REMAINING=$((COOLDOWN_PERIOD - TIME_SINCE_LAST_SCALE))
        echo "   ⏸️  En período de cooldown (${REMAINING}s restantes)"
        sleep $CHECK_INTERVAL
        continue
    fi
    
    # Obtener IDs de contenedores en ejecución
    CONTAINER_IDS=$(docker ps --filter "name=${SERVICE_NAME}" --format "{{.ID}}" 2>/dev/null)
    
    if [ -z "$CONTAINER_IDS" ]; then
        echo "   ⚠️  No hay contenedores en ejecución todavía"
        sleep $CHECK_INTERVAL
        continue
    fi
    
    # Obtener métricas de CPU
    TOTAL_CPU=0
    COUNT=0
    
    echo "   Contenedores encontrados:"
    for CONTAINER_ID in $CONTAINER_IDS; do
        CONTAINER_NAME=$(docker inspect --format '{{.Name}}' $CONTAINER_ID | sed 's/\///')
        
        # Obtener CPU del contenedor
        CPU_RAW=$(docker stats --no-stream --format "{{.CPUPerc}}" $CONTAINER_ID 2>/dev/null)
        
        if [ -n "$CPU_RAW" ]; then
            # Remover el símbolo % y convertir
            CPU=$(echo "$CPU_RAW" | sed 's/%//')
            echo "      - $CONTAINER_NAME: ${CPU}% CPU"
            
            # Sumar al total (usando bc para decimales)
            TOTAL_CPU=$(echo "$TOTAL_CPU + $CPU" | bc 2>/dev/null)
            COUNT=$((COUNT + 1))
        fi
    done
    
    if [ $COUNT -eq 0 ]; then
        echo "   ⚠️  No se pudieron obtener métricas de CPU"
        sleep $CHECK_INTERVAL
        continue
    fi
    
    # Calcular promedio
    AVG_CPU=$(echo "scale=2; $TOTAL_CPU / $COUNT" | bc 2>/dev/null)
    
    if [ -z "$AVG_CPU" ]; then
        echo "   ⚠️  Error calculando CPU promedio"
        sleep $CHECK_INTERVAL
        continue
    fi
    
    echo "   📈 CPU Promedio: ${AVG_CPU}%"
    
    # Decisión de escalado
    NEEDS_SCALE=false
    NEW_REPLICAS=$CURRENT_REPLICAS
    
    # Verificar si necesita escalar UP
    if (( $(echo "$AVG_CPU > $CPU_THRESHOLD_HIGH" | bc -l) )); then
        if [ "$CURRENT_REPLICAS" -lt "$MAX_REPLICAS" ]; then
            NEW_REPLICAS=$((CURRENT_REPLICAS + 1))
            echo "   🔼 CPU ALTA (${AVG_CPU}% > ${CPU_THRESHOLD_HIGH}%)"
            echo "   ➡️  Escalando de $CURRENT_REPLICAS a $NEW_REPLICAS réplicas"
            NEEDS_SCALE=true
        else
            echo "   ⚠️  CPU alta pero ya en MAX_REPLICAS ($MAX_REPLICAS)"
        fi
    # Verificar si necesita escalar DOWN
    elif (( $(echo "$AVG_CPU < $CPU_THRESHOLD_LOW" | bc -l) )); then
        if [ "$CURRENT_REPLICAS" -gt "$MIN_REPLICAS" ]; then
            NEW_REPLICAS=$((CURRENT_REPLICAS - 1))
            echo "   🔽 CPU BAJA (${AVG_CPU}% < ${CPU_THRESHOLD_LOW}%)"
            echo "   ➡️  Reduciendo de $CURRENT_REPLICAS a $NEW_REPLICAS réplicas"
            NEEDS_SCALE=true
        else
            echo "   ⚠️  CPU baja pero ya en MIN_REPLICAS ($MIN_REPLICAS)"
        fi
    else
        echo "   ✅ CPU en rango normal (${CPU_THRESHOLD_LOW}% - ${CPU_THRESHOLD_HIGH}%)"
    fi
    
    # Ejecutar escalado si es necesario
    if [ "$NEEDS_SCALE" = true ]; then
        echo "   🔄 Ejecutando: docker service scale $SERVICE_NAME=$NEW_REPLICAS"
        
        if docker service scale $SERVICE_NAME=$NEW_REPLICAS 2>&1; then
            echo "   ✅ Escalado exitoso"
            LAST_SCALE_TIME=$(date +%s)
            echo "   ⏸️  Iniciando cooldown de ${COOLDOWN_PERIOD}s"
        else
            echo "   ❌ Error en el escalado"
        fi
    fi
    
    echo "   ⏳ Próxima verificación en ${CHECK_INTERVAL}s..."
    sleep $CHECK_INTERVAL
done
