package com.marketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.marketplace.model.Mensaje;
import com.marketplace.model.User;

public interface MensajeRepository extends JpaRepository<Mensaje, Long>{

	    @Query("SELECT m.receptor.id FROM Mensaje m WHERE m.emisor.id = :userId")
	    List<Long> findReceptoresByEmisor(@Param("userId") Long userId);

	    @Query("SELECT m.emisor.id FROM Mensaje m WHERE m.receptor.id = :userId")
	    List<Long> findEmisoresByReceptor(@Param("userId") Long userId);
	}


