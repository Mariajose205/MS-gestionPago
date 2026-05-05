package com.fitlife.payment.repository;

import com.fitlife.payment.entity.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    
    Optional<MetodoPago> findByTipo(String tipo);
}
