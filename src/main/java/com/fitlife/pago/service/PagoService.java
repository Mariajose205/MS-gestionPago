package com.fitlife.pago.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitlife.pago.client.ReservaClient;
import com.fitlife.pago.entity.Pago;
import com.fitlife.pago.repository.PagoRepository;

@Service
@Transactional
public class PagoService {

    private static final Logger logger = LoggerFactory.getLogger(PagoService.class);

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ReservaClient reservaClient;

    // Temporalmente comentado hasta que el IDE reconozca la clase
    // @Autowired
    // private PagoEventPublisher pagoEventPublisher;

    // CRUD básico
    public Pago crearPago(Pago pago) {
        // Validar que el monto sea positivo
        if (pago.getMonto() == null || pago.getMonto() <= 0) {
            throw new RuntimeException("El monto del pago debe ser mayor que cero");
        }
        
        // Generar ID de transacción único
        pago.setIdTransaccion(UUID.randomUUID().toString());
        
        return pagoRepository.save(pago);
    }

    public Optional<Pago> obtenerPagoPorId(Long id) {
        return pagoRepository.findById(id);
    }

    public List<Pago> obtenerTodosLosPagos() {
        return pagoRepository.findAll();
    }

    public Pago actualizarPago(Long id, Pago pagoActualizado) {
        return pagoRepository.findById(id)
                .map(pago -> {
                    pago.setIdUsuario(pagoActualizado.getIdUsuario());
                    pago.setIdReserva(pagoActualizado.getIdReserva());
                    pago.setMonto(pagoActualizado.getMonto());
                    pago.setFechaPago(pagoActualizado.getFechaPago());
                    pago.setMetodoPago(pagoActualizado.getMetodoPago());
                    pago.setEstado(pagoActualizado.getEstado());
                    pago.setDescripcion(pagoActualizado.getDescripcion());
                    return pagoRepository.save(pago);
                })
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
    }

    public void eliminarPago(Long id) {
        pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
        pagoRepository.deleteById(id);
    }

    // Métodos de gestión de pagos
    public Pago procesarPago(Long id) {
        return pagoRepository.findById(id)
                .map(pago -> {
                    if (!pago.estaPendiente()) {
                        throw new RuntimeException("Solo se pueden procesar pagos en estado PENDIENTE");
                    }
                    
                    logger.info("💳 Procesando pago ID: {}", id);
                    
                    // Simular procesamiento de pago
                    boolean procesamientoExitoso = simularProcesamientoPago(pago);
                    
                    if (procesamientoExitoso) {
                        String codigoAutorizacion = generateAuthCode();
                        pago.confirmarPago(pago.getIdTransaccion(), codigoAutorizacion);
                        
                        // Confirmar la reserva en el microservicio de reservas
                        if (pago.getIdReserva() != null) {
                            logger.info("🔄 Intentando confirmar reserva {} tras pago exitoso", pago.getIdReserva());
                            boolean reservaConfirmada = reservaClient.confirmarReserva(pago.getIdReserva());
                            
                            if (reservaConfirmada) {
                                logger.info("✅ Reserva {} confirmada exitosamente", pago.getIdReserva());
                            } else {
                                logger.warn("⚠️ No se pudo confirmar la reserva {} en MS-reservas", pago.getIdReserva());
                            }
                        } else {
                            logger.warn("⚠️ Pago {} no tiene ID de reserva asociado", id);
                        }
                        
                        // TODO: Publicar eventos de pago exitoso cuando PagoEventPublisher esté disponible
                        // String emailUsuario = obtenerEmailUsuario(pago.getIdUsuario());
                        // pagoEventPublisher.publicarPagoExitoso(pago, emailUsuario);
                        
                        logger.info("✅ Pago {} procesado exitosamente", id);
                    } else {
                        pago.fallarPago("Error en el procesamiento del pago");
                        
                        // TODO: Publicar eventos de pago fallido cuando PagoEventPublisher esté disponible
                        // String emailUsuario = obtenerEmailUsuario(pago.getIdUsuario());
                        // pagoEventPublisher.publicarPagoFallido(pago, emailUsuario, "Error en procesamiento");
                        
                        logger.info("❌ Pago {} falló", id);
                    }
                    
                    return pagoRepository.save(pago);
                })
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
    }

    public Pago cancelarPago(Long id, String motivo) {
        return pagoRepository.findById(id)
                .map(pago -> {
                    if (!pago.estaPendiente()) {
                        throw new RuntimeException("Solo se pueden cancelar pagos en estado PENDIENTE");
                    }
                    pago.cancelarPago(motivo);
                    return pagoRepository.save(pago);
                })
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
    }

    public Pago devolverPago(Long id, Double monto, String motivo) {
        return pagoRepository.findById(id)
                .map(pago -> {
                    if (!pago.puedeDevolverse()) {
                        throw new RuntimeException("Este pago no puede ser devuelto");
                    }
                    
                    if (monto > pago.getMontoRestante()) {
                        throw new RuntimeException("El monto a devolver no puede exceder el monto restante");
                    }
                    
                    pago.devolverPago(monto, motivo);
                    return pagoRepository.save(pago);
                })
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
    }

    // Métodos de búsqueda
    public List<Pago> obtenerPagosPorUsuario(Long idUsuario) {
        return pagoRepository.findByIdUsuario(idUsuario);
    }

    public List<Pago> obtenerPagosPorReserva(Long idReserva) {
        return pagoRepository.findByIdReserva(idReserva);
    }

    public List<Pago> obtenerPagosPorEstado(String estado) {
        Pago.EstadoPago estadoEnum;
        try {
            estadoEnum = Pago.EstadoPago.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado no válido: " + estado);
        }
        return pagoRepository.findByEstado(estadoEnum);
    }

    public List<Pago> obtenerPagosPorMetodo(String metodo) {
        Pago.MetodoPago metodoEnum;
        try {
            metodoEnum = Pago.MetodoPago.valueOf(metodo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Método de pago no válido: " + metodo);
        }
        return pagoRepository.findByMetodoPago(metodoEnum);
    }

    public List<Pago> obtenerPagosPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return pagoRepository.findByFechaPagoBetween(inicio, fin);
    }

    // Métodos de estadísticas
    public long contarPagosCompletadosPorUsuario(Long idUsuario) {
        return pagoRepository.countPagosCompletadosPorUsuario(idUsuario);
    }

    public Double sumMontoPagadoPorUsuario(Long idUsuario) {
        return pagoRepository.sumMontoPagadoPorUsuario(idUsuario);
    }

    // Métodos de utilidad
    private boolean simularProcesamientoPago(Pago pago) {
        // Simulación de procesamiento de pago
        // En un entorno real, aquí se conectaría con pasarelas de pago
        try {
            // Simular delay de procesamiento
            Thread.sleep(1000);
            
            // Simular 90% de éxito
            return Math.random() < 0.9;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private String generateAuthCode() {
        return "AUTH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Método auxiliar para obtener email del usuario
     * En un entorno real, esto llamaría a MS-usuarios
     */
    private String obtenerEmailUsuario(Long idUsuario) {
        // Simulación - en producción llamaría al MS-usuarios
        // Por ahora, usamos un email basado en el ID
        return "usuario" + idUsuario + "@fitlife.com";
    }

    public List<Pago> obtenerPagosRecientes(int dias) {
        LocalDateTime fecha = LocalDateTime.now().minusDays(dias);
        return pagoRepository.findPagosRecientes(fecha);
    }

    public Double obtenerTotalPagadoPorUsuario(Long idUsuario) {
        Double total = sumMontoPagadoPorUsuario(idUsuario);
        return total != null ? total : 0.0;
    }

    // Validaciones
    public void validarPago(Pago pago) {
        if (pago.getIdUsuario() == null) {
            throw new RuntimeException("El ID de usuario es obligatorio");
        }
        
        if (pago.getMonto() == null || pago.getMonto() <= 0) {
            throw new RuntimeException("El monto debe ser mayor que cero");
        }
        
        if (pago.getMetodoPago() == null) {
            throw new RuntimeException("El método de pago es obligatorio");
        }
        
        if (pago.getFechaPago() == null) {
            throw new RuntimeException("La fecha de pago es obligatoria");
        }
    }
}
