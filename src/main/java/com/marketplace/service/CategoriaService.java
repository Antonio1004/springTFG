package com.marketplace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marketplace.model.Categoria;
import com.marketplace.model.User;
import com.marketplace.repository.CategoriaRepository;
import com.marketplace.repository.ProductoRepository;

@Service
public class CategoriaService {
	
	@Autowired
	CategoriaRepository categoriaRepository;
	
	@Autowired
	ProductoRepository productoRepository;
	
	
	// Listar todas las categorias
    public List<Categoria> getAllCategorias() {
        return categoriaRepository.findAll();
    }
    
    // Obtener categoria por ID
    public Categoria getCategoriaById(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }
    
    public Categoria createCategoria(String name) {

        String nombreLimpio = name.trim();

        // Validación: nombre no vacío
        if (nombreLimpio.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        // Validación: mínimo 3 caracteres
        if (nombreLimpio.length() < 3) {
            throw new IllegalArgumentException("El nombre debe tener al menos 3 caracteres");
        }

        // Validación: nombre repetido
        if (categoriaRepository.findByName(nombreLimpio).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        Categoria categoria = new Categoria();
        categoria.setName(nombreLimpio);
        return categoriaRepository.save(categoria);
    }

   

    // Eliminar categoría
    public void deleteCategoria(Long id) {

        // Verificar que exista
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La categoría no existe"));

        // Verificar si tiene productos asociados
        Long productosAsociados = productoRepository.countByCategoriaId(id);
        if (productosAsociados > 0) {
            throw new IllegalStateException("No se puede eliminar esta categoría porque tiene productos asignados");
        }

        // Eliminar
        categoriaRepository.delete(categoria);
    }
    public Categoria updateCategoria(Long id, String nuevoNombre) {

        String nombreLimpio = nuevoNombre.trim();

        // Validación: nombre no vacío
        if (nombreLimpio.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        // Validación: mínimo 3 caracteres
        if (nombreLimpio.length() < 3) {
            throw new IllegalArgumentException("El nombre debe tener al menos 3 caracteres");
        }

        // Obtener categoría a editar
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La categoría no existe"));

        // Validación: nombre repetido en OTRA categoría
        Optional<Categoria> categoriaExistente = categoriaRepository.findByName(nombreLimpio);

        if (categoriaExistente.isPresent() && !categoriaExistente.get().getId().equals(id)) {
            throw new IllegalArgumentException("Ya existe otra categoría con ese nombre");
        }

        categoria.setName(nombreLimpio);
        return categoriaRepository.save(categoria);
    }

}
