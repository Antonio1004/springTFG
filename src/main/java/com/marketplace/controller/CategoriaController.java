package com.marketplace.controller;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.marketplace.model.Categoria;
import com.marketplace.service.CategoriaService;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // Obtener todas las categorías
    @GetMapping("/list")
    public List<Categoria> getAllCategorias() {
        return categoriaService.getAllCategorias();
    }

    // Crear categoría
    @PostMapping("/create/{name}")
    public ResponseEntity<?> createCategoria(@PathVariable String name) {

        try {
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El nombre de la categoría no puede estar vacío"));
            }

            String nombreLimpio = name.trim();

            if (nombreLimpio.length() < 3) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "El nombre de la categoría debe tener al menos 3 caracteres"));
            }

            Categoria nueva = categoriaService.createCategoria(nombreLimpio);
            return ResponseEntity.ok(nueva);

        } catch (IllegalArgumentException e) {
            // Error de validación interna del servicio (duplicado, etc.)
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error inesperado al crear la categoría"));
        }
    }

    // Eliminar categoría
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategoria(@PathVariable Long id) {

        try {
            categoriaService.deleteCategoria(id);
            return ResponseEntity.ok(Map.of("message", "Categoría eliminada correctamente"));

        } catch (IllegalArgumentException e) {
            // Categoría no existe
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (IllegalStateException e) {
            // Tiene productos asociados
            return ResponseEntity.status(HttpStatus.SC_CONFLICT)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al eliminar la categoría"));
        }
    }

    // Editar categoría
    @PutMapping("/edit/{id}/{nuevoNombre}")
    public ResponseEntity<?> editarCategoria(
            @PathVariable Long id,
            @PathVariable String nuevoNombre) {

        try {
            Categoria actualizada = categoriaService.updateCategoria(id, nuevoNombre);

            return ResponseEntity.ok(Map.of(
                    "message", "Categoría actualizada correctamente",
                    "categoria", actualizada
            ));

        } catch (IllegalArgumentException e) {
            // Nombre inválido o categoría no encontrada
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al editar la categoría"));
        }
    }

    // Obtener categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoriaById(@PathVariable Long id) {

        Categoria categoria = categoriaService.getCategoriaById(id);

        if (categoria == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "La categoría no existe"));
        }

        return ResponseEntity.ok(categoria);
    }
}
