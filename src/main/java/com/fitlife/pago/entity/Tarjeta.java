package com.fitlife.pago.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tarjetas")
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de usuario es obligatorio")
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoTarjeta tipo = TipoTarjeta.CREDITO; // Valor por defecto

    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Size(min = 16, max = 19, message = "El número de tarjeta debe tener entre 16 y 19 caracteres")
    @Column(name = "numero", nullable = false, length = 19)
    private String numero;

    @NotBlank(message = "El titular es obligatorio")
    @Size(max = 100, message = "El nombre del titular no puede exceder 100 caracteres")
    @Column(name = "titular", nullable = false, length = 100)
    private String titular;

    @NotBlank(message = "La fecha de vencimiento es obligatoria")
    @Column(name = "fecha_vencimiento", nullable = false, length = 7)
    private String fechaVencimiento; // Formato MM/YY

    @NotBlank(message = "El CVV es obligatorio")
    @Size(min = 3, max = 4, message = "El CVV debe tener 3 o 4 dígitos")
    @Column(name = "cvv", nullable = false, length = 4)
    private String cvv;

    @Min(value = 0, message = "El saldo no puede ser negativo")
    @Column(name = "saldo", columnDefinition = "DECIMAL(15,2)")
    private Double saldo = 0.0; // Valor por defecto

    @Column(name = "por_defecto", nullable = false)
    private Boolean porDefecto = false;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "fecha_ultimo_uso")
    private LocalDateTime fechaUltimoUso;

    @Column(name = "limite_credito", columnDefinition = "DECIMAL(10,2)")
    private Double limiteCredito;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    // Constructores
    public Tarjeta() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Tarjeta(Long idUsuario, TipoTarjeta tipo, String numero, String titular, String fechaVencimiento, String cvv) {
        this();
        this.idUsuario = idUsuario;
        this.tipo = tipo;
        this.numero = numero;
        this.titular = titular;
        this.fechaVencimiento = fechaVencimiento;
        this.cvv = cvv;
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

    public TipoTarjeta getTipo() {
        return tipo;
    }

    public void setTipo(TipoTarjeta tipo) {
        this.tipo = tipo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Boolean getPorDefecto() {
        return porDefecto;
    }

    public void setPorDefecto(Boolean porDefecto) {
        this.porDefecto = porDefecto;
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

    public LocalDateTime getFechaUltimoUso() {
        return fechaUltimoUso;
    }

    public void setFechaUltimoUso(LocalDateTime fechaUltimoUso) {
        this.fechaUltimoUso = fechaUltimoUso;
    }

    public Double getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(Double limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Métodos de utilidad
    public boolean estaVencida() {
        if (fechaVencimiento == null || fechaVencimiento.length() != 7) return false;
        
        try {
            String[] partes = fechaVencimiento.split("/");
            int mes = Integer.parseInt(partes[0]);
            int anio = Integer.parseInt("20" + partes[1]);
            
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime vencimiento = LocalDateTime.of(anio, mes, 1, 0, 0).plusMonths(1).minusDays(1);
            
            return ahora.isAfter(vencimiento);
        } catch (Exception e) {
            return true; // Si no puede parsear, asumir vencida
        }
    }

    public boolean esCredito() {
        return tipo == TipoTarjeta.CREDITO;
    }

    public boolean esDebito() {
        return tipo == TipoTarjeta.DEBITO;
    }

    public boolean tieneSaldoDisponible(Double monto) {
        if (esDebito()) {
            return saldo != null && saldo >= monto;
        } else {
            // Para crédito, verifica contra límite
            double disponible = (limiteCredito != null ? limiteCredito : 0) - (saldo != null ? saldo : 0);
            return disponible >= monto;
        }
    }

    public void descontarSaldo(Double monto) {
        if (saldo == null) {
            saldo = 0.0;
        }
        saldo += monto; // Para crédito es positivo, para débito es negativo
        this.fechaUltimoUso = LocalDateTime.now();
    }

    public void reembolsarSaldo(Double monto) {
        if (saldo == null) {
            saldo = 0.0;
        }
        if (esDebito()) {
            saldo += monto; // Devolver al débito
        } else {
            saldo -= monto; // Reducir deuda de crédito
        }
    }

    public String getNumeroEnmascarado() {
        if (numero == null || numero.length() != 16) return "****";
        return "**** **** **** " + numero.substring(12);
    }

    public void marcarComoPredeterminada() {
        this.porDefecto = true;
    }

    public void quitarComoPredeterminada() {
        this.porDefecto = false;
    }

    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }

    // Enum para tipos de tarjeta
    public enum TipoTarjeta {
        CREDITO,
        DEBITO
    }
}
