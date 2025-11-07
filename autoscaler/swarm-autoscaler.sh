#!/bin/bash

SERVICE_NAME="petcare_app"
MAX_REPLICAS=5
MIN_REPLICAS=2
CPU_THRESHOLD_HIGH=30
CPU_THRESHOLD_LOW=20
CHECK_INTERVAL=10

echo "🚀 Iniciando autoescalador para $SERVICE_NAME"

while true; do
    CURRENT_REPLICAS=$(docker service ls --filter name=$SERVICE_NAME --format "{{.Replicas}}" | cut -d'/' -f1)
    TASK_IDS=$(docker service ps $SERVICE_NAME -q --filter desired-state=running)

    if [ -z "$TASK_IDS" ]; then
        echo "⚠️  No se encontraron tareas en ejecución"
        sleep $CHECK_INTERVAL
        continue
    fi

    TOTAL_CPU=0
    COUNT=0

    for TASK_ID in $TASK_IDS; do
        CONTAINER_ID=$(docker inspect $TASK_ID --format '{{.Status.ContainerStatus.ContainerID}}' 2>/dev/null)
        if [ -n "$CONTAINER_ID" ]; then
            CPU=$(docker stats --no-stream --format "{{.CPUPerc}}" $CONTAINER_ID | sed 's/%//')
            if [ -n "$CPU" ]; then
                TOTAL_CPU=$(echo "$TOTAL_CPU + $CPU" | bc)
                COUNT=$((COUNT + 1))
            fi
        fi
    done

    if [ $COUNT -eq 0 ]; then
        echo "⚠️  No se pudieron obtener métricas de CPU"
        sleep $CHECK_INTERVAL
        continue
    fi

    AVG_CPU=$(echo "scale=2; $TOTAL_CPU / $COUNT" | bc)
    echo "📊 [$(date '+%Y-%m-%d %H:%M:%S')] Réplicas: $CURRENT_REPLICAS | CPU promedio: ${AVG_CPU}%"

    if (( $(echo "$AVG_CPU > $CPU_THRESHOLD_HIGH" | bc -l) )) && [ "$CURRENT_REPLICAS" -lt "$MAX_REPLICAS" ]; then
        NEW_REPLICAS=$((CURRENT_REPLICAS + 1))
        echo "🔼 CPU alta (${AVG_CPU}%) - Escalando a $NEW_REPLICAS réplicas"
        docker service scale $SERVICE_NAME=$NEW_REPLICAS

    elif (( $(echo "$AVG_CPU < $CPU_THRESHOLD_LOW" | bc -l) )) && [ "$CURRENT_REPLICAS" -gt "$MIN_REPLICAS" ]; then
        NEW_REPLICAS=$((CURRENT_REPLICAS - 1))
        echo "🔽 CPU baja (${AVG_CPU}%) - Reduciendo a $NEW_REPLICAS réplicas"
        docker service scale $SERVICE_NAME=$NEW_REPLICAS
    fi

    sleep $CHECK_INTERVAL
done
