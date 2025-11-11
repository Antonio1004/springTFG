package com.marketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marketplace.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

}
