package com.fitlife.payment.service;

import com.fitlife.payment.dto.PagoRequest;
import com.fitlife.payment.dto.PagoResponse;
import com.fitlife.payment.entity.Pago;
import com.fitlife.payment.entity.MetodoPago;
import com.fitlife.payment.repository.PagoRepository;
import com.fitlife.payment.repository.MetodoPagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);

    private final PagoRepository pagoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final RabbitTemplate rabbitTemplate;
    
    public PagoService(PagoRepository pagoRepository, 
                      MetodoPagoRepository metodoPagoRepository,
                      RabbitTemplate rabbitTemplate) {
        this.pagoRepository = pagoRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Value("${rabbitmq.exchange.name:fitlife.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key.payment:payment.created}")
    private String paymentRoutingKey;

    @Transactional
    public PagoResponse crearPago(PagoRequest pagoRequest) {
        log.info("Creando pago para usuario: {}", pagoRequest.getIdUsuario());

        // Validar que el método de pago exista
        MetodoPago metodoPago = metodoPagoRepository.findById(pagoRequest.getIdMetodo())
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado con ID: " + pagoRequest.getIdMetodo()));
        
        // Validar que el método de pago sea válido
        if (metodoPago == null) {
            throw new RuntimeException("Método de pago inválido");
        }

        // Crear el pago
        Pago pago = new Pago();
        pago.setIdUsuario(pagoRequest.getIdUsuario());
        pago.setIdReserva(pagoRequest.getIdReserva());
        pago.setIdMetodo(pagoRequest.getIdMetodo());
        pago.setMonto(pagoRequest.getMonto());

        Pago pagoGuardado = pagoRepository.save(pago);
        log.info("Pago creado exitosamente con ID: {}", pagoGuardado.getIdPago());

        // Enviar mensaje a RabbitMQ para notificar el pago
        PaymentNotificationMessage notificationMessage = new PaymentNotificationMessage(
                pagoGuardado.getIdPago(),
                pagoGuardado.getIdUsuario(),
                pagoGuardado.getIdReserva(),
                pagoGuardado.getMonto(),
                pagoGuardado.getFechaPago()
        );

        rabbitTemplate.convertAndSend(exchangeName, paymentRoutingKey, notificationMessage);
        log.info("Mensaje de pago enviado a RabbitMQ");

        return convertirAResponse(pagoGuardado);
    }

    public List<PagoResponse> obtenerPagosPorUsuario(Long idUsuario) {
        return pagoRepository.findByIdUsuario(idUsuario).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<PagoResponse> obtenerPagosPorReserva(Long idReserva) {
        return pagoRepository.findByIdReserva(idReserva).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public PagoResponse obtenerPagoPorId(Long idPago) {
        Pago pago = pagoRepository.findById(idPago)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + idPago));
        return convertirAResponse(pago);
    }

    public List<PagoResponse> obtenerPagosPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pagoRepository.findByFechaPagoBetween(fechaInicio, fechaFin).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    private PagoResponse convertirAResponse(Pago pago) {
        PagoResponse response = new PagoResponse();
        response.setIdPago(pago.getIdPago());
        response.setIdUsuario(pago.getIdUsuario());
        response.setIdReserva(pago.getIdReserva());
        response.setIdMetodo(pago.getIdMetodo());
        response.setMonto(pago.getMonto());
        response.setFechaPago(pago.getFechaPago());
        response.setEstado("COMPLETADO");
        return response;
    }

    }
