package com.fitlife.payment.controller;

import com.fitlife.payment.dto.PagoRequest;
import com.fitlife.payment.dto.PagoResponse;
import com.fitlife.payment.service.PagoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagoController.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagoService pagoService;

    @Autowired
    private ObjectMapper objectMapper;

    private PagoRequest pagoRequest;
    private PagoResponse pagoResponse;

    @BeforeEach
    void setUp() {
        pagoRequest = new PagoRequest();
        pagoRequest.setIdUsuario(1L);
        pagoRequest.setIdReserva(1L);
        pagoRequest.setIdMetodo(1L);
        pagoRequest.setMonto(new BigDecimal("100.00"));

        pagoResponse = new PagoResponse();
        pagoResponse.setIdPago(1L);
        pagoResponse.setIdUsuario(1L);
        pagoResponse.setIdReserva(1L);
        pagoResponse.setIdMetodo(1L);
        pagoResponse.setMonto(new BigDecimal("100.00"));
        pagoResponse.setFechaPago(LocalDateTime.now());
        pagoResponse.setEstado("COMPLETADO");
    }

    @Test
    void crearPago_DebeRetornarCreated_CuandoPagoEsValido() throws Exception {
        // Given
        when(pagoService.crearPago(any(PagoRequest.class))).thenReturn(pagoResponse);

        // When & Then
        mockMvc.perform(post("/api/pagos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPago").value(1))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.monto").value(100.00))
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));

        verify(pagoService, times(1)).crearPago(any(PagoRequest.class));
    }

    @Test
    void crearPago_DebeRetornarBadRequest_CuandoPagoRequestEsInvalido() throws Exception {
        // Given
        PagoRequest invalidRequest = new PagoRequest();
        // No se establecen campos obligatorios

        // When & Then
        mockMvc.perform(post("/api/pagos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(pagoService, never()).crearPago(any());
    }

    @Test
    void obtenerPagosPorUsuario_DebeRetornarOk_CuandoUsuarioExiste() throws Exception {
        // Given
        List<PagoResponse> pagos = Arrays.asList(pagoResponse);
        when(pagoService.obtenerPagosPorUsuario(1L)).thenReturn(pagos);

        // When & Then
        mockMvc.perform(get("/api/pagos/usuario/{idUsuario}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].idUsuario").value(1))
                .andExpect(jsonPath("$[0].monto").value(100.00));

        verify(pagoService, times(1)).obtenerPagosPorUsuario(1L);
    }

    @Test
    void obtenerPagosPorReserva_DebeRetornarOk_CuandoReservaExiste() throws Exception {
        // Given
        List<PagoResponse> pagos = Arrays.asList(pagoResponse);
        when(pagoService.obtenerPagosPorReserva(1L)).thenReturn(pagos);

        // When & Then
        mockMvc.perform(get("/api/pagos/reserva/{idReserva}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].idReserva").value(1))
                .andExpect(jsonPath("$[0].monto").value(100.00));

        verify(pagoService, times(1)).obtenerPagosPorReserva(1L);
    }

    @Test
    void obtenerPagoPorId_DebeRetornarOk_CuandoPagoExiste() throws Exception {
        // Given
        when(pagoService.obtenerPagoPorId(1L)).thenReturn(pagoResponse);

        // When & Then
        mockMvc.perform(get("/api/pagos/{idPago}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPago").value(1))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.monto").value(100.00));

        verify(pagoService, times(1)).obtenerPagoPorId(1L);
    }

    @Test
    void obtenerPagosPorFecha_DebeRetornarOk_CuandoExistenPagos() throws Exception {
        // Given
        List<PagoResponse> pagos = Arrays.asList(pagoResponse);
        LocalDateTime fechaInicio = LocalDateTime.now().minusDays(1);
        LocalDateTime fechaFin = LocalDateTime.now().plusDays(1);
        
        when(pagoService.obtenerPagosPorFecha(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(pagos);

        // When & Then
        mockMvc.perform(get("/api/pagos/fecha")
                .param("fechaInicio", fechaInicio.toString())
                .param("fechaFin", fechaFin.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].monto").value(100.00));

        verify(pagoService, times(1)).obtenerPagosPorFecha(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void healthCheck_DebeRetornarOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/pagos/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Microservicio de Pagos funcionando correctamente"));
    }
}
