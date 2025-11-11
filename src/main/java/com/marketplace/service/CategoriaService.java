package com.marketplace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marketplace.model.Categoria;
import com.marketplace.model.User;
import com.marketplace.repository.CategoriaRepository;

@Service
public class CategoriaService {
	
	@Autowired
	CategoriaRepository categoriaRepository;
	// Listar todas las categorias
    public List<Categoria> getAllCategorias() {
        return categoriaRepository.findAll();
    }
    
    // Obtener categoria por ID
    public Categoria getCategoriaById(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }
    
}
