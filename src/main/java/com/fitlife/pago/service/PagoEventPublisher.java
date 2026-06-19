package com.fitlife.pago.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitlife.pago.dto.PagoEventDTO;
import com.fitlife.pago.entity.Pago;

@Service
public class PagoEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(PagoEventPublisher.class);

    // Temporalmente comentado hasta que RabbitMQ esté completamente configurado
    // @Autowired
    // private RabbitTemplate rabbitTemplate;

    @Value("${fitlife.exchange.reserva-confirmada}")
    private String reservaConfirmadaExchange;

    @Value("${fitlife.routing-key.cupo}")
    private String cupoRoutingKey;

    @Value("${fitlife.routing-key.mensaje}")
    private String mensajeRoutingKey;

    /**
     * Publica eventos cuando un pago es exitoso
     */
    public void publicarPagoExitoso(Pago pago, String emailUsuario) {
        try {
            logger.info("📤 Publicando eventos de pago exitoso para pago ID: {}", pago.getId());

            // Crear evento base
            PagoEventDTO evento = crearEventoBase(pago, emailUsuario, "PAGO_EXITOSO");

            // TODO: Implementar publicación cuando RabbitTemplate esté disponible
            // Publicar evento para actualización de cupos (MS-reservas)
            // publicarEventoCupoUpdate(evento);

            // Publicar evento para notificación (MS-notificaciones)
            // publicarEventoMensajeNotify(evento);

            logger.info("✅ Eventos de pago exitoso simulados (RabbitMQ temporalmente deshabilitado)");

        } catch (Exception e) {
            logger.error("❌ Error simulando eventos de pago exitoso: {}", e.getMessage(), e);
            throw new RuntimeException("Error simulando eventos de pago", e);
        }
    }

    /**
     * Publica eventos cuando un pago falla
     */
    public void publicarPagoFallido(Pago pago, String emailUsuario, String motivo) {
        try {
            logger.info("📤 Publicando eventos de pago fallido para pago ID: {}", pago.getId());

            // Crear evento base
            PagoEventDTO evento = crearEventoBase(pago, emailUsuario, "PAGO_FALLIDO");
            evento.setEstadoPago(pago.getEstado().name());

            // TODO: Implementar publicación cuando RabbitTemplate esté disponible
            // Publicar solo evento de notificación para pago fallido
            // rabbitTemplate.convertAndSend(
            //     reservaConfirmadaExchange,
            //     mensajeRoutingKey,
            //     evento
            // );

            logger.info("✅ Evento de pago fallido simulado (RabbitMQ temporalmente deshabilitado)");

        } catch (Exception e) {
            logger.error("❌ Error simulando evento de pago fallido: {}", e.getMessage(), e);
            throw new RuntimeException("Error simulando evento de pago fallido", e);
        }
    }

    /**
     * Publica evento para actualización de cupos en MS-reservas
     */
    private void publicarEventoCupoUpdate(PagoEventDTO evento) {
        logger.info("🎯 Publicando evento CupoUpdate para reserva ID: {}", evento.getIdReserva());
        
        // TODO: Implementar cuando RabbitTemplate esté disponible
        // rabbitTemplate.convertAndSend(
        //     reservaConfirmadaExchange,
        //     cupoRoutingKey,
        //     evento
        // );
        
        logger.info("📤 Evento CupoUpdate simulado");
    }

    /**
     * Publica evento para notificación en MS-notificaciones
     */
    private void publicarEventoMensajeNotify(PagoEventDTO evento) {
        logger.info("📧 Publicando evento MensajeNotify para usuario: {}", evento.getEmailUsuario());
        
        // TODO: Implementar cuando RabbitTemplate esté disponible
        // rabbitTemplate.convertAndSend(
        //     reservaConfirmadaExchange,
        //     mensajeRoutingKey,
        //     evento
        // );
        
        logger.info("📤 Evento MensajeNotify simulado");
    }

    /**
     * Crea un evento base con los datos del pago
     */
    private PagoEventDTO crearEventoBase(Pago pago, String emailUsuario, String tipoEvento) {
        PagoEventDTO evento = new PagoEventDTO();
        evento.setTipoEvento(tipoEvento);
        evento.setIdPago(pago.getId());
        evento.setIdReserva(pago.getIdReserva());
        evento.setIdUsuario(pago.getIdUsuario());
        evento.setEmailUsuario(emailUsuario);
        evento.setMonto(pago.getMonto());
        evento.setMetodoPago(pago.getMetodoPago().name());
        evento.setFechaPago(pago.getFechaPago());
        evento.setEstadoPago(pago.getEstado().name());
        
        if (pago.getCodigoAutorizacion() != null) {
            evento.setCodigoAutorizacion(pago.getCodigoAutorizacion());
        }
        
        return evento;
    }
}
