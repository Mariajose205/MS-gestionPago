package com.fitlife.pago.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "id_reserva")
    private Long idReserva;

    @NotNull(message = "El monto es obligatorio")
    @Min(value = 0, message = "El monto no puede ser negativo")
    @Column(name = "monto", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private Double monto;

    @NotNull(message = "La fecha de pago es obligatoria")
    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MetodoPago metodoPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(name = "id_transaccion", length = 100)
    private String idTransaccion;

    @Column(name = "codigo_autorizacion", length = 50)
    private String codigoAutorizacion;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @Column(name = "monto_devuelto", columnDefinition = "DECIMAL(10,2)")
    private Double montoDevuelto;

    @Column(name = "motivo_devolucion", length = 500)
    private String motivoDevolucion;

    // Constructores
    public Pago() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaPago = LocalDateTime.now();
    }

    public Pago(Long idUsuario, Double monto, MetodoPago metodoPago) {
        this();
        this.idUsuario = idUsuario;
        this.monto = monto;
        this.metodoPago = metodoPago;
    }

    public Pago(Long idUsuario, Long idReserva, Double monto, MetodoPago metodoPago) {
        this(idUsuario, monto, metodoPago);
        this.idReserva = idReserva;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getCodigoAutorizacion() {
        return codigoAutorizacion;
    }

    public void setCodigoAutorizacion(String codigoAutorizacion) {
        this.codigoAutorizacion = codigoAutorizacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public LocalDateTime getFechaConfirmacion() {
        return fechaConfirmacion;
    }

    public void setFechaConfirmacion(LocalDateTime fechaConfirmacion) {
        this.fechaConfirmacion = fechaConfirmacion;
    }

    public Double getMontoDevuelto() {
        return montoDevuelto;
    }

    public void setMontoDevuelto(Double montoDevuelto) {
        this.montoDevuelto = montoDevuelto;
    }

    public String getMotivoDevolucion() {
        return motivoDevolucion;
    }

    public void setMotivoDevolucion(String motivoDevolucion) {
        this.motivoDevolucion = motivoDevolucion;
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Métodos de utilidad
    public boolean estaPendiente() {
        return estado == EstadoPago.PENDIENTE;
    }

    public boolean estaCompletado() {
        return estado == EstadoPago.COMPLETADO;
    }

    public boolean estaFallido() {
        return estado == EstadoPago.FALLIDO;
    }

    public boolean estaDevuelto() {
        return estado == EstadoPago.DEVUELTO;
    }

    public boolean estaCancelado() {
        return estado == EstadoPago.CANCELADO;
    }

    public void confirmarPago(String idTransaccion, String codigoAutorizacion) {
        this.estado = EstadoPago.COMPLETADO;
        this.idTransaccion = idTransaccion;
        this.codigoAutorizacion = codigoAutorizacion;
        this.fechaConfirmacion = LocalDateTime.now();
    }

    public void fallarPago(String motivo) {
        this.estado = EstadoPago.FALLIDO;
        this.descripcion = motivo;
    }

    public void cancelarPago(String motivo) {
        this.estado = EstadoPago.CANCELADO;
        this.descripcion = motivo;
    }

    public void devolverPago(Double montoDevuelto, String motivo) {
        this.estado = EstadoPago.DEVUELTO;
        this.montoDevuelto = montoDevuelto;
        this.motivoDevolucion = motivo;
    }

    public boolean puedeDevolverse() {
        return estaCompletado() && (montoDevuelto == null || montoDevuelto == 0);
    }

    public Double getMontoRestante() {
        if (montoDevuelto == null) return monto;
        return monto - montoDevuelto;
    }

    // Enums
    public enum MetodoPago {
        TARJETA_CREDITO,
        TARJETA_DEBITO,
        TRANSFERENCIA,
        EFECTIVO,
        WEBPAY,
        MERCADO_PAGO
    }

    public enum EstadoPago {
        PENDIENTE,
        COMPLETADO,
        FALLIDO,
        DEVUELTO,
        CANCELADO
    }
}
