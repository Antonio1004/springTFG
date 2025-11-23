package com.marketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marketplace.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long>{
	
	List<Producto> findAllByVendedorId(Long idVendedor);
	List<Producto> findAllByVendido(String vendido);
	List<Producto> findAllByCompradorId(Long compradorId);
    boolean existsByVendedorIdAndCompradorId(Long vendedorId, Long compradorId);


}
