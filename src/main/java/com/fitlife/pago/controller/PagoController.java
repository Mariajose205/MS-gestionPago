package com.fitlife.pago.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitlife.pago.dto.PagoEventDTO;
import com.fitlife.pago.entity.Pago;
import com.fitlife.pago.service.PagoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pagos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    // Temporalmente comentado hasta que el IDE reconozca la clase
    // @Autowired
    // private PagoEventPublisher pagoEventPublisher;

    // CRUD básico
    @PostMapping
    public ResponseEntity<Pago> crearPago(@Valid @RequestBody Pago pago) {
        try {
            pagoService.validarPago(pago);
            Pago nuevoPago = pagoService.crearPago(pago);
            return new ResponseEntity<>(nuevoPago, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtenerPagoPorId(@PathVariable Long id) {
        Optional<Pago> pago = pagoService.obtenerPagoPorId(id);
        return pago.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Pago>> obtenerTodosLosPagos() {
        List<Pago> pagos = pagoService.obtenerTodosLosPagos();
        return new ResponseEntity<>(pagos, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pago> actualizarPago(@PathVariable Long id, @Valid @RequestBody Pago pago) {
        try {
            pagoService.validarPago(pago);
            Pago pagoActualizado = pagoService.actualizarPago(id, pago);
            return new ResponseEntity<>(pagoActualizado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPago(@PathVariable Long id) {
        try {
            pagoService.eliminarPago(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Métodos de gestión de pagos
    @PostMapping("/{id}/procesar")
    public ResponseEntity<Pago> procesarPago(@PathVariable Long id) {
        try {
            Pago pagoProcesado = pagoService.procesarPago(id);
            return new ResponseEntity<>(pagoProcesado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Pago> cancelarPago(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String motivo = request.get("motivo");
            Pago pagoCancelado = pagoService.cancelarPago(id, motivo);
            return new ResponseEntity<>(pagoCancelado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/devolver")
    public ResponseEntity<Pago> devolverPago(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Double monto = ((Number) request.get("monto")).doubleValue();
            String motivo = (String) request.get("motivo");
            Pago pagoDevuelto = pagoService.devolverPago(id, monto, motivo);
            return new ResponseEntity<>(pagoDevuelto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Métodos de búsqueda
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Pago>> obtenerPagosPorUsuario(@PathVariable Long idUsuario) {
        List<Pago> pagos = pagoService.obtenerPagosPorUsuario(idUsuario);
        return new ResponseEntity<>(pagos, HttpStatus.OK);
    }

    @GetMapping("/reserva/{idReserva}")
    public ResponseEntity<List<Pago>> obtenerPagosPorReserva(@PathVariable Long idReserva) {
        List<Pago> pagos = pagoService.obtenerPagosPorReserva(idReserva);
        return new ResponseEntity<>(pagos, HttpStatus.OK);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pago>> obtenerPagosPorEstado(@PathVariable String estado) {
        try {
            List<Pago> pagos = pagoService.obtenerPagosPorEstado(estado);
            return new ResponseEntity<>(pagos, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/metodo/{metodo}")
    public ResponseEntity<List<Pago>> obtenerPagosPorMetodo(@PathVariable String metodo) {
        try {
            List<Pago> pagos = pagoService.obtenerPagosPorMetodo(metodo);
            return new ResponseEntity<>(pagos, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<Pago>> obtenerPagosPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        List<Pago> pagos = pagoService.obtenerPagosPorRangoFechas(inicio, fin);
        return new ResponseEntity<>(pagos, HttpStatus.OK);
    }

    // Métodos de estadísticas
    @GetMapping("/usuario/{idUsuario}/completados/count")
    public ResponseEntity<Long> contarPagosCompletadosPorUsuario(@PathVariable Long idUsuario) {
        long count = pagoService.contarPagosCompletadosPorUsuario(idUsuario);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/usuario/{idUsuario}/total")
    public ResponseEntity<Double> obtenerTotalPagadoPorUsuario(@PathVariable Long idUsuario) {
        Double total = pagoService.obtenerTotalPagadoPorUsuario(idUsuario);
        return new ResponseEntity<>(total, HttpStatus.OK);
    }

    @GetMapping("/recientes")
    public ResponseEntity<List<Pago>> obtenerPagosRecientes(@RequestParam(defaultValue = "7") int dias) {
        List<Pago> pagos = pagoService.obtenerPagosRecientes(dias);
        return new ResponseEntity<>(pagos, HttpStatus.OK);
    }

    // Endpoint de prueba para enviar eventos de pago
    @PostMapping("/test/pago-event")
    public ResponseEntity<String> enviarEventoPagoPrueba(@RequestBody PagoEventDTO pagoEvent) {
        try {
            // TODO: Implementar cuando PagoEventPublisher esté disponible
            // Simular pago para el evento
            // Pago pagoSimulado = new Pago();
            // pagoSimulado.setId(pagoEvent.getIdPago());
            // pagoSimulado.setIdReserva(pagoEvent.getIdReserva());
            // pagoSimulado.setIdUsuario(pagoEvent.getIdUsuario());
            // pagoSimulado.setMonto(pagoEvent.getMonto());
            // pagoSimulado.setFechaPago(pagoEvent.getFechaPago());
            // pagoSimulado.setMetodoPago(Pago.MetodoPago.valueOf(pagoEvent.getMetodoPago()));
            // pagoSimulado.setEstado(Pago.EstadoPago.valueOf(pagoEvent.getEstadoPago()));
            // 
            // if (pagoEvent.getCodigoAutorizacion() != null) {
            //     pagoSimulado.setCodigoAutorizacion(pagoEvent.getCodigoAutorizacion());
            // }
            // 
            // // Publicar evento
            // if ("PAGO_EXITOSO".equals(pagoEvent.getTipoEvento())) {
            //     pagoEventPublisher.publicarPagoExitoso(pagoSimulado, pagoEvent.getEmailUsuario());
            // } else if ("PAGO_FALLIDO".equals(pagoEvent.getTipoEvento())) {
            //     pagoEventPublisher.publicarPagoFallido(pagoSimulado, pagoEvent.getEmailUsuario(), "Error de prueba");
            // }
            
            return new ResponseEntity<>("Evento de pago recibido: " + pagoEvent.getTipoEvento() + " (PagoEventPublisher temporalmente deshabilitado)", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error procesando evento: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("MS-gestionPago is running!", HttpStatus.OK);
    }
}
