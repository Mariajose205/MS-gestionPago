# Usar imagen base de Maven para construir la aplicación
FROM maven:3.9.6-openjdk-17 AS build

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo pom.xml primero para aprovechar el cache de Maven
COPY pom.xml .

# Descargar las dependencias
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Usar imagen base de OpenJDK para ejecutar la aplicación
FROM openjdk:17-jre-slim

# Instalar curl para health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Crear directorio de la aplicación
WORKDIR /app

# Copiar el JAR construido desde la etapa de build
COPY --from=build /app/target/payment-management-1.0.0.jar app.jar

# Exponer el puerto
EXPOSE 8081

# Crear usuario no root para seguridad
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/api/pagos/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
