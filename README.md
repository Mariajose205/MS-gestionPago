# Fitlife MS-gestionPago

Microservicio de gestión de pagos para FitLife. Este servicio maneja el procesamiento de pagos, gestión de tarjetas y notificaciones de eventos de pago.

## Características

- **Procesamiento de Pagos**: Gestión completa de pagos de membresías y clases
- **Gestión de Tarjetas**: CRUD para tarjetas de crédito/débito
- **Eventos de Pago**: Publicación de eventos de pago a RabbitMQ
- **Integración con Reservas**: Cliente para comunicación con microservicio de reservas
- **REST API**: Endpoints completos para gestión de pagos
- **Unit Testing**: Pruebas unitarias con JUnit y Mockito

## Tecnologías

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- RabbitMQ
- MySQL 8.0
- Maven
- Docker

## Endpoints

### Gestión de Pagos
- `POST /pagos` - Crear nuevo pago
- `GET /pagos` - Obtener todos los pagos
- `GET /pagos/{id}` - Obtener pago por ID
- `PUT /pagos/{id}` - Actualizar pago
- `DELETE /pagos/{id}` - Eliminar pago
- `GET /pagos/usuario/{usuarioId}` - Obtener pagos por usuario

### Gestión de Tarjetas
- `POST /tarjetas` - Registrar nueva tarjeta
- `GET /tarjetas` - Obtener todas las tarjetas
- `GET /tarjetas/{id}` - Obtener tarjeta por ID
- `DELETE /tarjetas/{id}` - Eliminar tarjeta

## Configuración

### Variables de Entorno

```env
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-pagos:3306/fitlife_pagos_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_RABBITMQ_HOST=rabbitmq
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest
```

## Desarrollo

### Compilar el proyecto
```bash
mvn clean package
```

### Ejecutar pruebas
```bash
mvn test
```

### Ejecutar localmente
```bash
mvn spring-boot:run
```

## Docker

### Construir imagen
```bash
docker build -t fitlife-gestionpago:latest .
```

### Ejecutar contenedor
```bash
docker run -p 8086:8086 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/fitlife_pagos_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  -e SPRING_RABBITMQ_HOST=host.docker.internal \
  fitlife-gestionpago:latest
```

## GitHub Actions

Este repositorio utiliza GitHub Actions para CI/CD:

- **Build**: Compila el proyecto con Maven
- **Test**: Ejecuta pruebas unitarias
- **Docker Build**: Construye la imagen Docker
- **Docker Push**: Sube la imagen a Docker Hub

## Contribución

1. Fork el repositorio
2. Crea una rama para tu feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -m 'Agrega nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

## Licencia

Este proyecto es parte de FitLife Gym Management System.
