# Decisión de Estrategia de Balanceo

## Estrategia Seleccionada: Round Robin

## Justificación:
- Distribución equitativa de carga entre las réplicas
- Simple de implementar y entender
- Adecuado para este caso donde las peticiones son similares en carga

## Alternativas Consideradas:
- IP Hash: Descartada porque no necesitamos afinidad de sesión
- Least Connections: Descartada porque nuestras peticiones son cortas


