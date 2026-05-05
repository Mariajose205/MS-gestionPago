package com.fitlife.payment.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
public class Pago {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Long idPago;
    
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;
    
    @Column(name = "id_reserva")
    private Long idReserva;
    
    @Column(name = "id_metodo", nullable = false)
    private Long idMetodo;
    
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    
    public Pago() {}
    
    public Pago(Long idPago, Long idUsuario, Long idReserva, Long idMetodo, BigDecimal monto, LocalDateTime fechaPago) {
        this.idPago = idPago;
        this.idUsuario = idUsuario;
        this.idReserva = idReserva;
        this.idMetodo = idMetodo;
        this.monto = monto;
        this.fechaPago = fechaPago;
    }
    
    public Long getIdPago() {
        return idPago;
    }
    
    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }
    
    public Long getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public Long getIdReserva() {
        return idReserva;
    }
    
    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }
    
    public Long getIdMetodo() {
        return idMetodo;
    }
    
    public void setIdMetodo(Long idMetodo) {
        this.idMetodo = idMetodo;
    }
    
    public BigDecimal getMonto() {
        return monto;
    }
    
    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    
    public LocalDateTime getFechaPago() {
        return fechaPago;
    }
    
    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
    
    @PrePersist
    protected void onCreate() {
        fechaPago = LocalDateTime.now();
    }
}
