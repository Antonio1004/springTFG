package com.marketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.marketplace.model.Mensaje;
import com.marketplace.model.User;

public interface MensajeRepository extends JpaRepository<Mensaje, Long>{

	@Query("""
		    SELECT m FROM Mensaje m
		    WHERE 
		        (
		            (m.emisor.id = :user1 AND m.receptor.id = :user2)
		            OR
		            (m.emisor.id = :user2 AND m.receptor.id = :user1)
		        )
		        AND m.producto.id = :productoId
		    ORDER BY m.fecha ASC
		""")
		List<Mensaje> obtenerConversacionEntreUsuariosYProducto(
		        @Param("user1") Long user1,
		        @Param("user2") Long user2,
		        @Param("productoId") Long productoId);

	@Query("SELECT m FROM Mensaje m WHERE m.emisor.id = :userId OR m.receptor.id = :userId")
	List<Mensaje> findMensajesRelacionados(@Param("userId") Long userId);

	
	@Modifying
	@Query("DELETE FROM Mensaje m WHERE m.producto.id = :productoId")
	void deleteByProductoId(@Param("productoId") Long productoId);
	}






