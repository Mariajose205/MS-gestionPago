package com.fitlife.payment.controller;

import com.fitlife.payment.dto.PagoRequest;
import com.fitlife.payment.dto.PagoResponse;
import com.fitlife.payment.service.PagoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@CrossOrigin(origins = "*")
public class PagoController {

    private static final Logger log = LoggerFactory.getLogger(PagoController.class);
    private final PagoService pagoService;
    
    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    public ResponseEntity<PagoResponse> crearPago(@Valid @RequestBody PagoRequest pagoRequest) {
        log.info("Solicitud para crear pago recibida para usuario: {}", pagoRequest.getIdUsuario());
        PagoResponse response = pagoService.crearPago(pagoRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PagoResponse>> obtenerPagosPorUsuario(@PathVariable Long idUsuario) {
        log.info("Obteniendo pagos para usuario: {}", idUsuario);
        List<PagoResponse> pagos = pagoService.obtenerPagosPorUsuario(idUsuario);
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<List<PagoResponse>> obtenerPagosPorReserva(@PathVariable Long idReserva) {
        log.info("Obteniendo pagos para reserva: {}", idReserva);
        List<PagoResponse> pagos = pagoService.obtenerPagosPorReserva(idReserva);
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/{idPago}")
    public ResponseEntity<PagoResponse> obtenerPagoPorId(@PathVariable Long idPago) {
        log.info("Obteniendo pago con ID: {}", idPago);
        PagoResponse pago = pagoService.obtenerPagoPorId(idPago);
        return ResponseEntity.ok(pago);
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<PagoResponse>> obtenerPagosPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        log.info("Obteniendo pagos entre fechas: {} y {}", fechaInicio, fechaFin);
        List<PagoResponse> pagos = pagoService.obtenerPagosPorFecha(fechaInicio, fechaFin);
        return ResponseEntity.ok(pagos);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Microservicio de Pagos funcionando correctamente");
    }
}
