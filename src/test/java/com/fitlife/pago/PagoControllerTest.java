package com.fitlife.pago;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fitlife.pago.controller.PagoController;
import com.fitlife.pago.entity.Pago;
import com.fitlife.pago.service.PagoService;

@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    @Mock
    private PagoService pagoService;

    @InjectMocks
    private PagoController pagoController;

    private Pago pago;

    @BeforeEach
    void setUp() {
        pago = new Pago();
        pago.setId(1L);
        pago.setIdUsuario(1L);
        pago.setIdReserva(1L);
        pago.setMonto(15000.0);
        pago.setFechaPago(LocalDateTime.now());
        pago.setMetodoPago(Pago.MetodoPago.TARJETA_CREDITO);
        pago.setEstado(Pago.EstadoPago.COMPLETADO);
    }

    @Test
    void testCrearPago_Success() {
        doNothing().when(pagoService).validarPago(any(Pago.class));
        when(pagoService.crearPago(any(Pago.class))).thenReturn(pago);

        ResponseEntity<Pago> response = pagoController.crearPago(pago);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        
        verify(pagoService, times(1)).validarPago(any(Pago.class));
        verify(pagoService, times(1)).crearPago(any(Pago.class));
    }

    @Test
    void testCrearPago_ValidationFailed() {
        doThrow(new RuntimeException("Validación fallida")).when(pagoService).validarPago(any(Pago.class));

        ResponseEntity<Pago> response = pagoController.crearPago(pago);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(pagoService, times(1)).validarPago(any(Pago.class));
        verify(pagoService, never()).crearPago(any(Pago.class));
    }

    @Test
    void testObtenerPagoPorId_Success() {
        when(pagoService.obtenerPagoPorId(1L)).thenReturn(Optional.of(pago));

        ResponseEntity<Pago> response = pagoController.obtenerPagoPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        
        verify(pagoService, times(1)).obtenerPagoPorId(1L);
    }

    @Test
    void testObtenerPagoPorId_NotFound() {
        when(pagoService.obtenerPagoPorId(999L)).thenReturn(Optional.empty());

        ResponseEntity<Pago> response = pagoController.obtenerPagoPorId(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        
        verify(pagoService, times(1)).obtenerPagoPorId(999L);
    }

    @Test
    void testObtenerTodosLosPagos_Success() {
        when(pagoService.obtenerTodosLosPagos()).thenReturn(List.of(pago));

        ResponseEntity<List<Pago>> response = pagoController.obtenerTodosLosPagos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        
        verify(pagoService, times(1)).obtenerTodosLosPagos();
    }

    @Test
    void testActualizarPago_Success() {
        Pago pagoActualizado = new Pago();
        pagoActualizado.setId(1L);
        pagoActualizado.setMonto(20000.0);
        pagoActualizado.setEstado(Pago.EstadoPago.PENDIENTE);

        doNothing().when(pagoService).validarPago(any(Pago.class));
        when(pagoService.actualizarPago(eq(1L), any(Pago.class))).thenReturn(pagoActualizado);

        ResponseEntity<Pago> response = pagoController.actualizarPago(1L, pagoActualizado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(pagoService, times(1)).validarPago(any(Pago.class));
        verify(pagoService, times(1)).actualizarPago(eq(1L), any(Pago.class));
    }

    @Test
    void testActualizarPago_NotFound() {
        doNothing().when(pagoService).validarPago(any(Pago.class));
        when(pagoService.actualizarPago(eq(1L), any(Pago.class))).thenThrow(new RuntimeException("Pago no encontrado"));

        ResponseEntity<Pago> response = pagoController.actualizarPago(1L, pago);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        verify(pagoService, times(1)).validarPago(any(Pago.class));
        verify(pagoService, times(1)).actualizarPago(eq(1L), any(Pago.class));
    }

    @Test
    void testEliminarPago_Success() {
        doNothing().when(pagoService).eliminarPago(1L);

        ResponseEntity<Void> response = pagoController.eliminarPago(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        verify(pagoService, times(1)).eliminarPago(1L);
    }

    @Test
    void testEliminarPago_NotFound() {
        doThrow(new RuntimeException("Pago no encontrado")).when(pagoService).eliminarPago(1L);

        ResponseEntity<Void> response = pagoController.eliminarPago(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        verify(pagoService, times(1)).eliminarPago(1L);
    }
}
