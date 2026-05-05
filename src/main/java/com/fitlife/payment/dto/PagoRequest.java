package com.fitlife.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class PagoRequest {
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;
    
    private Long idReserva;
    
    @NotNull(message = "El ID del método de pago es obligatorio")
    private Long idMetodo;
    
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser positivo")
    private BigDecimal monto;
    
    public PagoRequest() {}
    
    public PagoRequest(Long idUsuario, Long idReserva, Long idMetodo, BigDecimal monto) {
        this.idUsuario = idUsuario;
        this.idReserva = idReserva;
        this.idMetodo = idMetodo;
        this.monto = monto;
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
}
