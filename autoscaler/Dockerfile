FROM alpine:3.19

# Instalar dependencias necesarias
RUN apk add --no-cache \
    bash \
    bc \
    docker-cli \
    curl

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el script
COPY swarm-autoscaler.sh .

# Convertir fin de línea a Unix (LF) y dar permisos de ejecución
RUN sed -i 's/\r$//' swarm-autoscaler.sh && \
    chmod +x swarm-autoscaler.sh

# Healthcheck para verificar que el contenedor está corriendo
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD pgrep -f swarm-autoscaler.sh || exit 1

# Ejecutar el script explícitamente con bash
CMD ["/bin/bash", "./swarm-autoscaler.sh"]
