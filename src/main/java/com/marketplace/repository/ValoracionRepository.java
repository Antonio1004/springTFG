package com.marketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marketplace.model.Valoracion;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long>{
    Valoracion findByCompradorIdAndVendedorId(Long idComprador, Long idVendedor);
    List<Valoracion> findAllByVendedorId(Long vendedorId);


}
