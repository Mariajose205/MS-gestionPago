-- Base de datos para MS-gestionPago
CREATE DATABASE IF NOT EXISTS fitlife_pagos_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fitlife_pagos_db;

-- Tabla de pagos
CREATE TABLE IF NOT EXISTS pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_reserva BIGINT,
    monto DECIMAL(10,2) NOT NULL,
    fecha_pago TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodo_pago VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    id_transaccion VARCHAR(100),
    codigo_autorizacion VARCHAR(50),
    descripcion VARCHAR(500),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    fecha_confirmacion TIMESTAMP NULL,
    monto_devuelto DECIMAL(10,2),
    motivo_devolucion VARCHAR(500),
    
    INDEX idx_id_usuario (id_usuario),
    INDEX idx_id_reserva (id_reserva),
    INDEX idx_estado (estado),
    INDEX idx_metodo_pago (metodo_pago),
    INDEX idx_fecha_pago (fecha_pago),
    INDEX idx_fecha_creacion (fecha_creacion),
    INDEX idx_id_transaccion (id_transaccion),
    
    CONSTRAINT chk_monto CHECK (monto >= 0),
    CONSTRAINT chk_monto_devuelto CHECK (monto_devuelto IS NULL OR monto_devuelto >= 0),
    CONSTRAINT chk_estado CHECK (estado IN ('PENDIENTE', 'COMPLETADO', 'FALLIDO', 'DEVUELTO', 'CANCELADO')),
    CONSTRAINT chk_metodo_pago CHECK (metodo_pago IN ('TARJETA_CREDITO', 'TARJETA_DEBITO', 'TRANSFERENCIA', 'EFECTIVO', 'WEBPAY', 'MERCADO_PAGO'))
);

-- Tabla de tarjetas
CREATE TABLE IF NOT EXISTS tarjetas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    numero VARCHAR(16) NOT NULL,
    titular VARCHAR(100) NOT NULL,
    fecha_vencimiento VARCHAR(7) NOT NULL,
    cvv VARCHAR(4) NOT NULL,
    saldo DECIMAL(10,2),
    por_defecto BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    fecha_ultimo_uso TIMESTAMP NULL,
    limite_credito DECIMAL(10,2),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    
    INDEX idx_id_usuario (id_usuario),
    INDEX idx_tipo (tipo),
    INDEX idx_numero (numero),
    INDEX idx_activo (activo),
    INDEX idx_por_defecto (por_defecto),
    INDEX idx_fecha_creacion (fecha_creacion),
    
    CONSTRAINT chk_tipo CHECK (tipo IN ('CREDITO', 'DEBITO')),
    CONSTRAINT chk_saldo CHECK (saldo IS NULL OR saldo >= 0),
    CONSTRAINT chk_limite_credito CHECK (limite_credito IS NULL OR limite_credito >= 0),
    CONSTRAINT chk_numero_length CHECK (LENGTH(numero) = 16),
    CONSTRAINT chk_cvv_length CHECK (LENGTH(cvv) BETWEEN 3 AND 4)
);

-- Datos de ejemplo para pagos
INSERT INTO pagos (id_usuario, id_reserva, monto, metodo_pago, estado, id_transaccion, codigo_autorizacion, fecha_confirmacion) VALUES
(1, 1, 15000.00, 'TARJETA_CREDITO', 'COMPLETADO', 'TXN-12345678', 'AUTH-ABC123', NOW()),
(1, 2, 25000.00, 'TARJETA_DEBITO', 'COMPLETADO', 'TXN-87654321', 'AUTH-XYZ789', NOW()),
(2, 3, 12000.00, 'TRANSFERENCIA', 'PENDIENTE', 'TXN-11111111', NULL, NULL),
(3, 4, 18000.00, 'WEBPAY', 'COMPLETADO', 'TXN-22222222', 'AUTH-DEF456', NOW()),
(2, 5, 45000.00, 'TARJETA_CREDITO', 'COMPLETADO', 'TXN-33333333', 'AUTH-GHI789', NOW()),
(1, 6, 20000.00, 'MERCADO_PAGO', 'COMPLETADO', 'TXN-44444444', 'AUTH-JKL012', NOW()),
(3, 7, 15000.00, 'TARJETA_CREDITO', 'CANCELADO', 'TXN-55555555', NULL, NULL),
(4, 8, 30000.00, 'EFECTIVO', 'PENDIENTE', 'TXN-66666666', NULL, NULL),
(2, 9, 16000.00, 'TARJETA_DEBITO', 'COMPLETADO', 'TXN-77777777', 'AUTH-MNO345', NOW()),
(5, 10, 35000.00, 'WEBPAY', 'COMPLETADO', 'TXN-88888888', 'AUTH-PQR678', NOW()),
(1, 11, 22000.00, 'TARJETA_CREDITO', 'COMPLETADO', 'TXN-99999999', 'AUTH-STU901', NOW()),
(3, 12, 19000.00, 'TRANSFERENCIA', 'PENDIENTE', 'TXN-10101010', NULL, NULL),
(4, 13, 14000.00, 'MERCADO_PAGO', 'FALLIDO', 'TXN-13131313', NULL, NULL),
(2, 14, 13000.00, 'TARJETA_DEBITO', 'COMPLETADO', 'TXN-14141414', 'AUTH-VWX234', NOW()),
(5, 15, 28000.00, 'WEBPAY', 'COMPLETADO', 'TXN-15151515', 'AUTH-YZA567', NOW());

-- Datos de ejemplo para tarjetas
INSERT INTO tarjetas (id_usuario, tipo, numero, titular, fecha_vencimiento, cvv, saldo, por_defecto, limite_credito) VALUES
(1, 'CREDITO', '1111222233334444', 'Juan Perez', '12/25', '123', 5000.00, TRUE, 100000.00),
(1, 'DEBITO', '5555666677778888', 'Juan Perez', '08/24', '456', 25000.00, FALSE, NULL),
(2, 'CREDITO', '9999000011112222', 'Maria Garcia', '03/26', '789', 15000.00, TRUE, 150000.00),
(2, 'DEBITO', '3333444455556666', 'Maria Garcia', '11/23', '012', 8000.00, FALSE, NULL),
(3, 'CREDITO', '7777888899990000', 'Carlos Lopez', '07/25', '345', 12000.00, TRUE, 80000.00),
(4, 'DEBITO', '1111222233334444', 'Ana Martinez', '09/24', '678', 35000.00, TRUE, NULL),
(4, 'CREDITO', '5555666677778888', 'Ana Martinez', '01/26', '901', 8000.00, FALSE, 120000.00),
(5, 'CREDITO', '9999000011112222', 'Luis Rodriguez', '06/25', '234', 22000.00, TRUE, 200000.00),
(5, 'DEBITO', '3333444455556666', 'Luis Rodriguez', '12/23', '567', 15000.00, FALSE, NULL);
