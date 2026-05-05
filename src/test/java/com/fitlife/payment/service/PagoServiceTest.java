package com.fitlife.payment.service;

import com.fitlife.payment.dto.PagoRequest;
import com.fitlife.payment.dto.PagoResponse;
import com.fitlife.payment.entity.Pago;
import com.fitlife.payment.entity.MetodoPago;
import com.fitlife.payment.repository.PagoRepository;
import com.fitlife.payment.repository.MetodoPagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private MetodoPagoRepository metodoPagoRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PagoService pagoService;

    private PagoRequest pagoRequest;
    private Pago pago;
    private MetodoPago metodoPago;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        pagoRequest = new PagoRequest();
        pagoRequest.setIdUsuario(1L);
        pagoRequest.setIdReserva(1L);
        pagoRequest.setIdMetodo(1L);
        pagoRequest.setMonto(new BigDecimal("100.00"));

        metodoPago = new MetodoPago();

        pago = new Pago();
        pago.setIdPago(1L);
        pago.setIdUsuario(1L);
        pago.setIdReserva(1L);
        pago.setIdMetodo(1L);
        pago.setMonto(new BigDecimal("100.00"));
        pago.setFechaPago(LocalDateTime.now());

        // Configurar valores de RabbitMQ
        ReflectionTestUtils.setField(pagoService, "exchangeName", "fitlife.exchange");
        ReflectionTestUtils.setField(pagoService, "paymentRoutingKey", "payment.created");
    }

    @Test
    void crearPago_DebeRetornarPagoResponse_CuandoPagoEsValido() {
        // Given
        when(metodoPagoRepository.findById(1L)).thenReturn(Optional.of(metodoPago));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        // When
        PagoResponse result = pagoService.crearPago(pagoRequest);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getIdPago());
        assertEquals(1L, result.getIdUsuario());
        assertEquals(1L, result.getIdReserva());
        assertEquals(new BigDecimal("100.00"), result.getMonto());
        assertEquals("COMPLETADO", result.getEstado());

        // Verificar que se envió el mensaje a RabbitMQ
        verify(rabbitTemplate, times(1)).convertAndSend(eq("fitlife.exchange"), eq("payment.created"), any(PaymentNotificationMessage.class));
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    void crearPago_DebeLanzarExcepcion_CuandoMetodoPagoNoExiste() {
        // Given
        when(metodoPagoRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pagoService.crearPago(pagoRequest);
        });

        assertEquals("Método de pago no encontrado con ID: 1", exception.getMessage());
        verify(pagoRepository, never()).save(any());
        verify(rabbitTemplate, never()).convertAndSend(any(String.class), any(String.class), any(PaymentNotificationMessage.class));
    }

    @Test
    void obtenerPagosPorUsuario_DebeRetornarListaDePagos_CuandoUsuarioTienePagos() {
        // Given
        List<Pago> pagos = Arrays.asList(pago);
        when(pagoRepository.findByIdUsuario(1L)).thenReturn(pagos);

        // When
        List<PagoResponse> result = pagoService.obtenerPagosPorUsuario(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getIdUsuario());
        verify(pagoRepository, times(1)).findByIdUsuario(1L);
    }

    @Test
    void obtenerPagosPorReserva_DebeRetornarListaDePagos_CuandoReservaTienePagos() {
        // Given
        List<Pago> pagos = Arrays.asList(pago);
        when(pagoRepository.findByIdReserva(1L)).thenReturn(pagos);

        // When
        List<PagoResponse> result = pagoService.obtenerPagosPorReserva(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getIdReserva());
        verify(pagoRepository, times(1)).findByIdReserva(1L);
    }

    @Test
    void obtenerPagoPorId_DebeRetornarPago_CuandoPagoExiste() {
        // Given
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(pago));

        // When
        PagoResponse result = pagoService.obtenerPagoPorId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getIdPago());
        verify(pagoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPagoPorId_DebeLanzarExcepcion_CuandoPagoNoExiste() {
        // Given
        when(pagoRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pagoService.obtenerPagoPorId(1L);
        });

        assertEquals("Pago no encontrado con ID: 1", exception.getMessage());
        verify(pagoRepository, times(1)).findById(1L);
    }

    @Test
    void obtenerPagosPorFecha_DebeRetornarListaDePagos_CuandoExistenPagosEnRango() {
        // Given
        LocalDateTime fechaInicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fechaFin = LocalDateTime.now().plusDays(1);
        List<Pago> pagos = Arrays.asList(pago);
        
        when(pagoRepository.findByFechaPagoBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(pagos);

        // When
        List<PagoResponse> result = pagoService.obtenerPagosPorFecha(fechaInicio, fechaFin);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(pagoRepository, times(1)).findByFechaPagoBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}
