package com.fitlife.payment.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentNotificationMessage(
        Long idPago,
        Long idUsuario,
        Long idReserva,
        BigDecimal monto,
        LocalDateTime fechaPago
) {}
