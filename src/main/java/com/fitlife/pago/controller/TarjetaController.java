package com.fitlife.pago.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;

import com.fitlife.pago.entity.Tarjeta;
import com.fitlife.pago.service.TarjetaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pagos/tarjetas")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TarjetaController {

    @Autowired
    private TarjetaService tarjetaService;

    // CRUD básico
    @PostMapping
    public ResponseEntity<?> guardarTarjeta(@RequestBody Tarjeta tarjeta) {
        try {
            // Log para debugging
            System.out.println("=== DATOS RECIBIDOS ===");
            System.out.println("idUsuario: " + tarjeta.getIdUsuario());
            System.out.println("tipo: " + tarjeta.getTipo());
            System.out.println("numero: " + (tarjeta.getNumero() != null ? tarjeta.getNumero().substring(0, Math.min(4, tarjeta.getNumero().length())) + "****" : null));
            System.out.println("titular: " + tarjeta.getTitular());
            System.out.println("fechaVencimiento: " + tarjeta.getFechaVencimiento());
            System.out.println("cvv: " + (tarjeta.getCvv() != null ? "***" : null));
            System.out.println("saldo: " + tarjeta.getSaldo() + " (tipo: " + (tarjeta.getSaldo() != null ? tarjeta.getSaldo().getClass().getSimpleName() : "null") + ")");
            System.out.println("porDefecto: " + tarjeta.getPorDefecto());
            System.out.println("========================");
            
            tarjetaService.validarTarjeta(tarjeta);
            Tarjeta nuevaTarjeta = tarjetaService.guardarTarjeta(tarjeta);
            return new ResponseEntity<>(nuevaTarjeta, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Log del error
            System.out.println("ERROR AL GUARDAR TARJETA: " + e.getMessage());
            e.printStackTrace();
            
            // Devolver mensaje de error específico para debugging
            return new ResponseEntity<>(
                Map.of("error", e.getMessage(), "tarjetaRecibida", Map.of(
                    "idUsuario", tarjeta.getIdUsuario(),
                    "tipo", tarjeta.getTipo(),
                    "numero", tarjeta.getNumero() != null ? tarjeta.getNumero().substring(0, Math.min(4, tarjeta.getNumero().length())) + "****" : null,
                    "titular", tarjeta.getTitular(),
                    "fechaVencimiento", tarjeta.getFechaVencimiento(),
                    "cvv", tarjeta.getCvv() != null ? "***" : null,
                    "saldo", tarjeta.getSaldo(),
                    "porDefecto", tarjeta.getPorDefecto()
                )), 
                HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            // Capturar cualquier otra excepción
            System.out.println("ERROR INESPERADO: " + e.getMessage());
            e.printStackTrace();
            
            return new ResponseEntity<>(
                Map.of("error", "Error inesperado: " + e.getMessage()), 
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarjeta> obtenerTarjetaPorId(@PathVariable Long id) {
        Optional<Tarjeta> tarjeta = tarjetaService.obtenerTarjetaPorId(id);
        return tarjeta.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Tarjeta>> obtenerTodasLasTarjetas() {
        List<Tarjeta> tarjetas = tarjetaService.obtenerTodasLasTarjetas();
        return new ResponseEntity<>(tarjetas, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarjeta> actualizarTarjeta(@PathVariable Long id, @Valid @RequestBody Tarjeta tarjeta) {
        try {
            tarjetaService.validarTarjeta(tarjeta);
            Tarjeta tarjetaActualizada = tarjetaService.actualizarTarjeta(id, tarjeta);
            return new ResponseEntity<>(tarjetaActualizada, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarjeta(@PathVariable Long id) {
        try {
            tarjetaService.eliminarTarjeta(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Métodos especializados
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Tarjeta>> obtenerTarjetasPorUsuario(@PathVariable Long idUsuario) {
        List<Tarjeta> tarjetas = tarjetaService.obtenerTarjetasPorUsuario(idUsuario);
        return new ResponseEntity<>(tarjetas, HttpStatus.OK);
    }

    @GetMapping("/usuario/{idUsuario}/predeterminada")
    public ResponseEntity<Tarjeta> obtenerTarjetaPredeterminada(@PathVariable Long idUsuario) {
        Optional<Tarjeta> tarjeta = tarjetaService.obtenerTarjetaPredeterminada(idUsuario);
        return tarjeta.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}/predeterminada/{idUsuario}")
    public ResponseEntity<Tarjeta> marcarComoPredeterminada(@PathVariable Long id, @PathVariable Long idUsuario) {
        try {
            Tarjeta tarjeta = tarjetaService.marcarComoPredeterminada(id, idUsuario);
            return new ResponseEntity<>(tarjeta, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/saldo")
    public ResponseEntity<Void> actualizarSaldoTarjeta(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Double saldo = ((Number) request.get("saldo")).doubleValue();
            tarjetaService.actualizarSaldoTarjeta(id, saldo);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/descontar")
    public ResponseEntity<Void> descontarSaldoTarjeta(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Double monto = ((Number) request.get("monto")).doubleValue();
            tarjetaService.descontarSaldoTarjeta(id, monto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/reembolsar")
    public ResponseEntity<Void> reembolsarSaldoTarjeta(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Double monto = ((Number) request.get("monto")).doubleValue();
            tarjetaService.reembolsarSaldoTarjeta(id, monto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarTarjeta(@PathVariable Long id) {
        try {
            tarjetaService.desactivarTarjeta(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activarTarjeta(@PathVariable Long id) {
        try {
            tarjetaService.activarTarjeta(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Métodos de consulta
    @GetMapping("/usuario/{idUsuario}/count")
    public ResponseEntity<Long> contarTarjetasActivasPorUsuario(@PathVariable Long idUsuario) {
        long count = tarjetaService.contarTarjetasActivasPorUsuario(idUsuario);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/usuario/{idUsuario}/tiene-predeterminada")
    public ResponseEntity<Boolean> tieneTarjetaPredeterminada(@PathVariable Long idUsuario) {
        boolean tiene = tarjetaService.tieneTarjetaPredeterminada(idUsuario);
        return new ResponseEntity<>(tiene, HttpStatus.OK);
    }

    @GetMapping("/usuario/{idUsuario}/credito")
    public ResponseEntity<List<Tarjeta>> obtenerTarjetasDeCreditoPorUsuario(@PathVariable Long idUsuario) {
        List<Tarjeta> tarjetas = tarjetaService.obtenerTarjetasDeCreditoPorUsuario(idUsuario);
        return new ResponseEntity<>(tarjetas, HttpStatus.OK);
    }

    @GetMapping("/usuario/{idUsuario}/debito")
    public ResponseEntity<List<Tarjeta>> obtenerTarjetasDeDebitoPorUsuario(@PathVariable Long idUsuario) {
        List<Tarjeta> tarjetas = tarjetaService.obtenerTarjetasDeDebitoPorUsuario(idUsuario);
        return new ResponseEntity<>(tarjetas, HttpStatus.OK);
    }
}
