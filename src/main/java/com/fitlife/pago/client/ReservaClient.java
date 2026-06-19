package com.fitlife.pago.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class ReservaClient {

    private static final Logger logger = LoggerFactory.getLogger(ReservaClient.class);
    
    private final WebClient webClient;
    private final String msReservasUrl;

    public ReservaClient(@Value("${ms.reservas.url:http://localhost:8082}") String msReservasUrl) {
        this.msReservasUrl = msReservasUrl;
        this.webClient = WebClient.builder()
                .baseUrl(msReservasUrl)
                .build();
    }

    /**
     * Confirma una reserva en el microservicio de reservas con Circuit Breaker y Retry
     * @param idReserva ID de la reserva a confirmar
     * @return true si la confirmación fue exitosa, false en caso contrario
     */
    @CircuitBreaker(name = "pagosCB", fallbackMethod = "confirmarReservaFallback")
    @Retry(name = "pagosRetry")
    public boolean confirmarReserva(Long idReserva) {
        try {
            logger.info("🔄 Intentando confirmar reserva ID: {} en MS-reservas", idReserva);
            
            String response = webClient.patch()
                    .uri("/reservas/{id}/confirmar", idReserva)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            logger.info("✅ Reserva {} confirmada exitosamente en MS-reservas", idReserva);
            return true;
            
        } catch (WebClientResponseException e) {
            logger.error("❌ Error al confirmar reserva {}: Status {}, Body: {}", 
                    idReserva, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error al confirmar reserva: " + e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Error de conexión al confirmar reserva {}: {}", idReserva, e.getMessage());
            throw new RuntimeException("Error de conexión: " + e.getMessage());
        }
    }

    /**
     * Fallback method para confirmarReserva cuando el Circuit Breaker está abierto
     * @param idReserva ID de la reserva
     * @param exception Excepción que causó el fallback
     * @return false indicando que no se pudo confirmar la reserva
     */
    public boolean confirmarReservaFallback(Long idReserva, Exception exception) {
        logger.warn("⚠️ Circuit Breaker activado para confirmarReserva. Usando fallback para reserva {}", idReserva);
        logger.warn("⚠️ Motivo: {}", exception.getMessage());
        // Aquí podrías implementar lógica alternativa, como:
        // - Guardar la solicitud en una cola para procesamiento posterior
        // - Notificar al usuario que la confirmación se procesará más tarde
        // - Registrar el evento para auditoría
        return false;
    }

    /**
     * Verifica si una reserva existe en el microservicio de reservas con Circuit Breaker
     * @param idReserva ID de la reserva a verificar
     * @return true si la reserva existe, false en caso contrario
     */
    @CircuitBreaker(name = "pagosCB", fallbackMethod = "verificarReservaFallback")
    @Retry(name = "pagosRetry")
    public boolean verificarReserva(Long idReserva) {
        try {
            logger.info("🔍 Verificando existencia de reserva ID: {}", idReserva);
            
            String response = webClient.get()
                    .uri("/reservas/{id}", idReserva)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            logger.info("✅ Reserva {} existe en MS-reservas", idReserva);
            return true;
            
        } catch (WebClientResponseException.NotFound e) {
            logger.warn("⚠️ Reserva {} no encontrada en MS-reservas", idReserva);
            return false;
        } catch (Exception e) {
            logger.error("❌ Error al verificar reserva {}: {}", idReserva, e.getMessage());
            throw new RuntimeException("Error al verificar reserva: " + e.getMessage());
        }
    }

    /**
     * Fallback method para verificarReserva cuando el Circuit Breaker está abierto
     * @param idReserva ID de la reserva
     * @param exception Excepción que causó el fallback
     * @return false indicando que no se pudo verificar la reserva
     */
    public boolean verificarReservaFallback(Long idReserva, Exception exception) {
        logger.warn("⚠️ Circuit Breaker activado para verificarReserva. Usando fallback para reserva {}", idReserva);
        logger.warn("⚠️ Motivo: {}", exception.getMessage());
        // En caso de que el servicio de reservas no esté disponible,
        // asumimos que la reserva no existe para evitar inconsistencias
        return false;
    }

    /**
     * Obtiene información de una reserva con Circuit Breaker
     * @param idReserva ID de la reserva
     * @return String con la información de la reserva o null si no existe
     */
    @CircuitBreaker(name = "pagosCB", fallbackMethod = "obtenerReservaFallback")
    @Retry(name = "pagosRetry")
    public String obtenerReserva(Long idReserva) {
        try {
            logger.info("📋 Obteniendo información de reserva ID: {}", idReserva);
            
            String response = webClient.get()
                    .uri("/reservas/{id}", idReserva)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            logger.info("✅ Información de reserva {} obtenida", idReserva);
            return response;
            
        } catch (WebClientResponseException.NotFound e) {
            logger.warn("⚠️ Reserva {} no encontrada en MS-reservas", idReserva);
            return null;
        } catch (Exception e) {
            logger.error("❌ Error al obtener reserva {}: {}", idReserva, e.getMessage());
            throw new RuntimeException("Error al obtener reserva: " + e.getMessage());
        }
    }

    /**
     * Fallback method para obtenerReserva cuando el Circuit Breaker está abierto
     * @param idReserva ID de la reserva
     * @param exception Excepción que causó el fallback
     * @return null indicando que no se pudo obtener la reserva
     */
    public String obtenerReservaFallback(Long idReserva, Exception exception) {
        logger.warn("⚠️ Circuit Breaker activado para obtenerReserva. Usando fallback para reserva {}", idReserva);
        logger.warn("⚠️ Motivo: {}", exception.getMessage());
        return null;
    }
}
