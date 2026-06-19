package com.fitlife.pago.dto;

import java.time.LocalDateTime;

public class PagoEventDTO {
    
    private String tipoEvento;
    private Long idPago;
    private Long idReserva;
    private Long idUsuario;
    private String emailUsuario;
    private Double monto;
    private String metodoPago;
    private LocalDateTime fechaPago;
    private String estadoPago;
    private String codigoAutorizacion;
    
    // Getters and Setters
    public String getTipoEvento() {
        return tipoEvento;
    }
    
    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }
    
    public Long getIdPago() {
        return idPago;
    }
    
    public void setIdPago(Long idPago) {
        this.idPago = idPago;
    }
    
    public Long getIdReserva() {
        return idReserva;
    }
    
    public void setIdReserva(Long idReserva) {
        this.idReserva = idReserva;
    }
    
    public Long getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getEmailUsuario() {
        return emailUsuario;
    }
    
    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }
    
    public Double getMonto() {
        return monto;
    }
    
    public void setMonto(Double monto) {
        this.monto = monto;
    }
    
    public String getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public LocalDateTime getFechaPago() {
        return fechaPago;
    }
    
    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
    
    public String getEstadoPago() {
        return estadoPago;
    }
    
    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }
    
    public String getCodigoAutorizacion() {
        return codigoAutorizacion;
    }
    
    public void setCodigoAutorizacion(String codigoAutorizacion) {
        this.codigoAutorizacion = codigoAutorizacion;
    }
    
    @Override
    public String toString() {
        return "PagoEventDTO{" +
                "tipoEvento='" + tipoEvento + '\'' +
                ", idPago=" + idPago +
                ", idReserva=" + idReserva +
                ", idUsuario=" + idUsuario +
                ", emailUsuario='" + emailUsuario + '\'' +
                ", monto=" + monto +
                ", metodoPago='" + metodoPago + '\'' +
                ", fechaPago=" + fechaPago +
                ", estadoPago='" + estadoPago + '\'' +
                ", codigoAutorizacion='" + codigoAutorizacion + '\'' +
                '}';
    }
}
