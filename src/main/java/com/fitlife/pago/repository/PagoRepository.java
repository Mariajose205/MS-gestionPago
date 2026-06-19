package com.fitlife.pago.repository;

import com.fitlife.pago.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByIdUsuario(Long idUsuario);
    List<Pago> findByIdReserva(Long idReserva);
    List<Pago> findByEstado(Pago.EstadoPago estado);
    List<Pago> findByMetodoPago(Pago.MetodoPago metodoPago);
    
    List<Pago> findByIdUsuarioAndEstado(Long idUsuario, Pago.EstadoPago estado);
    List<Pago> findByIdReservaAndEstado(Long idReserva, Pago.EstadoPago estado);
    
    List<Pago> findByFechaPagoBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Pago> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    
    @Query("SELECT p FROM Pago p WHERE p.fechaCreacion >= :fecha ORDER BY p.fechaCreacion DESC")
    List<Pago> findPagosRecientes(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT COUNT(p) FROM Pago p WHERE p.idUsuario = :idUsuario AND p.estado = 'COMPLETADO'")
    long countPagosCompletadosPorUsuario(@Param("idUsuario") Long idUsuario);
    
    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.idUsuario = :idUsuario AND p.estado = 'COMPLETADO'")
    Double sumMontoPagadoPorUsuario(@Param("idUsuario") Long idUsuario);
    
    @Query("SELECT p.estado, COUNT(p) FROM Pago p GROUP BY p.estado")
    List<Object[]> countByEstado();
    
    @Query("SELECT DATE(p.fechaCreacion), COUNT(p) FROM Pago p GROUP BY DATE(p.fechaCreacion) ORDER BY DATE(p.fechaCreacion) DESC")
    List<Object[]> countByFecha();
}
