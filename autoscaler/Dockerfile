FROM alpine:3.19

# Instalar bash, bc y cliente Docker
RUN apk add --no-cache bash bc docker-cli

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el script
COPY swarm-autoscaler.sh .

# Dar permisos de ejecución
RUN chmod +x swarm-autoscaler.sh

# Ejecutar el script
CMD ["./swarm-autoscaler.sh"]
