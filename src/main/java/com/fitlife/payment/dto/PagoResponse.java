package com.fitlife.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoResponse {
    
    private Long idPago;
    private Long idUsuario;
    private Long idReserva;
    private Long idMetodo;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private String estado;
    
    public PagoResponse() {}
    
    public PagoResponse(Long idPago, Long idUsuario, Long idReserva, Long idMetodo, BigDecimal monto, LocalDateTime fechaPago, String estado) {
        this.idPago = idPago;
        this.idUsuario = idUsuario;
        this.idReserva = idReserva;
        this.idMetodo = idMetodo;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.estado = estado;
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
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
