# Prueba de Integración: Pago → Reserva

## Flujo Implementado

1. **Creación de Reserva**: Se crea una reserva en MS-reservas (estado: PENDIENTE)
2. **Proceso de Pago**: Se procesa un pago en MS-gestionPago
3. **Confirmación Automática**: Si el pago es exitoso, se confirma automáticamente la reserva en MS-reservas

## Configuración

- **MS-reservas**: `http://localhost:8082/api`
- **MS-gestionPago**: `http://localhost:8086/api`

## Pasos para Probar

### 1. Iniciar los microservicios
```bash
# Terminal 1 - MS-reservas
cd MS-reservas
mvn spring-boot:run

# Terminal 2 - MS-gestionPago  
cd MS-gestionPago
mvn spring-boot:run
```

### 2. Crear una reserva
```bash
curl -X POST http://localhost:8082/api/reservas \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": 1,
    "idHorario": 1,
    "idLocation": 1,
    "fechaClase": "2026-05-08T10:00:00",
    "numeroPersonas": 2,
    "montoTotal": 100.0
  }'
```

### 3. Crear un pago asociado a la reserva
```bash
curl -X POST http://localhost:8086/api/pagos \
  -H "Content-Type: application/json" \
  -d '{
    "idUsuario": 1,
    "idReserva": 1,
    "monto": 100.0,
    "fechaPago": "2026-05-07T14:00:00",
    "metodoPago": "TARJETA_CREDITO",
    "descripcion": "Pago de reserva"
  }'
```

### 4. Procesar el pago (esto confirmará automáticamente la reserva)
```bash
curl -X POST http://localhost:8086/api/pagos/1/procesar
```

### 5. Verificar que la reserva está confirmada
```bash
curl -X GET http://localhost:8082/api/reservas/1
```

## Resultados Esperados

- ✅ Pago procesado exitosamente (estado: COMPLETADO)
- ✅ Reserva confirmada automáticamente (estado: ACTIVA)
- 📝 Logs en MS-gestionPago mostrando la comunicación con MS-reservas

## Logs a Observar

En MS-gestionPago deberías ver:
```
Procesando pago ID: 1
Intentando confirmar reserva 1 tras pago exitoso
Reserva 1 confirmada exitosamente
Pago 1 procesado exitosamente
```

## Troubleshooting

Si la reserva no se confirma:
1. Verifica que MS-reservas esté corriendo en el puerto 8082
2. Revisa los logs de ambos microservicios
3. Verifica la configuración de red y firewall
