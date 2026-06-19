package com.fitlife.pago.repository;

import com.fitlife.pago.entity.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, Long> {

    List<Tarjeta> findByIdUsuario(Long idUsuario);
    List<Tarjeta> findByIdUsuarioAndActivoTrue(Long idUsuario);
    
    Optional<Tarjeta> findByIdUsuarioAndPorDefectoTrue(Long idUsuario);
    
    List<Tarjeta> findByTipoAndActivoTrue(Tarjeta.TipoTarjeta tipo);
    
    @Query("SELECT t FROM Tarjeta t WHERE t.idUsuario = :idUsuario AND t.activo = true ORDER BY t.porDefecto DESC, t.fechaCreacion DESC")
    List<Tarjeta> findTarjetasActivasPorUsuarioOrdenadas(@Param("idUsuario") Long idUsuario);
    
    @Query("SELECT COUNT(t) FROM Tarjeta t WHERE t.idUsuario = :idUsuario AND t.activo = true")
    long countTarjetasActivasPorUsuario(@Param("idUsuario") Long idUsuario);
    
    @Query("SELECT COUNT(t) FROM Tarjeta t WHERE t.idUsuario = :idUsuario AND t.porDefecto = true AND t.activo = true")
    long countTarjetasPredeterminadasPorUsuario(@Param("idUsuario") Long idUsuario);
    
    boolean existsByNumero(String numero);
    boolean existsByIdUsuarioAndNumero(Long idUsuario, String numero);
}
