package com.marketplace.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.marketplace.model.Categoria;
import com.marketplace.model.ImagenDTO;
import com.marketplace.model.Producto;
import com.marketplace.model.ProductoDTO;
import com.marketplace.service.CategoriaService;

import com.marketplace.service.ProductoService;
import com.marketplace.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@RequestMapping("/productos")
@Tag(name = "Productos", description = "Controlador para la gestión de Productos en ReVende")

public class ProductoController {
	
	@Autowired
	CategoriaService categoriaService;
	
	@Autowired
	ProductoService productoService;
	
	@Autowired
	UserService userService;
	
	
	
	@GetMapping("/misPublicaciones/{idVendedor}")
    public ResponseEntity<List<ProductoDTO>> getMisProductos(@PathVariable Long idVendedor) {
        List<Producto> productos = productoService.getAllProductosByVendedorId(idVendedor);

        List<ProductoDTO> dtos = productos.stream().map(p -> {
            List<ImagenDTO> imagenes = p.getListaImagenes().stream()
                                        .map(img -> new ImagenDTO(img.getId(), img.getUrl()))
                                        .collect(Collectors.toList());

            return new ProductoDTO(
                    p.getId(),
                    p.getTitle(),
                    p.getEstado(),
                    p.getPrice(),
                    p.getDescripcion(),
                    p.getFecha_publicacion() != null ? p.getFecha_publicacion().toString() : null,
                    p.getFecha_venta() != null ? p.getFecha_venta().toString() : null,
                    p.getVendido(),
                    p.getCategoria() != null ? p.getCategoria().getId() : null,
                    p.getCategoria() != null ? p.getCategoria().getName() : null,
                    p.getVendedor() != null ? p.getVendedor().getId() : null,
                    p.getComprador() != null ? p.getComprador().getId() : null,
                    imagenes
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
    
	// GET para devolver las categorías cuando se cargue la pantalla de añadir producto
    @GetMapping("/vender")
    public ResponseEntity<List<Categoria>> getCategorias() {
        List<Categoria> categorias = categoriaService.getAllCategorias();
        return ResponseEntity.ok(categorias);
    }

    @PostMapping("/vender")
    public ResponseEntity<?> crearProducto(
            @RequestPart("producto") ProductoDTO productoDTO,
            @RequestPart("imagenes") List<MultipartFile> imagenes) {

        try {
            // Guardar producto y sus imágenes usando el service
            Producto productoGuardado = productoService.guardarProductoConImagenes(productoDTO, imagenes);

            // Preparar DTO de respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Producto creado correctamente");

            // Devolver solo información relevante del producto y URLs de las imágenes
            Map<String, Object> productoResponse = new HashMap<>();
            productoResponse.put("id", productoGuardado.getId());
            productoResponse.put("title", productoGuardado.getTitle());
            productoResponse.put("descripcion", productoGuardado.getDescripcion());
            productoResponse.put("price", productoGuardado.getPrice());
            productoResponse.put("estado", productoGuardado.getEstado());
            productoResponse.put("categoriaId", productoGuardado.getCategoria().getId());
            productoResponse.put("vendedorId", productoGuardado.getVendedor().getId());
            productoResponse.put("imagenesUrls", productoGuardado.getListaImagenes().stream()
                    .map(img -> img.getUrl())
                    .toList());

            response.put("producto", productoResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear producto: " + e.getMessage());
        }
    }
    
    
    @GetMapping("/edit/{id}")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Long id) {
        try {
            Producto p = productoService.getProductoById(id); // <-- Hay que implementarlo en el service

            if (p == null) {
                return ResponseEntity.notFound().build();
            }

            List<ImagenDTO> imagenes = p.getListaImagenes() != null
                    ? p.getListaImagenes().stream()
                        .map(img -> new ImagenDTO(img.getId(), img.getUrl()))
                        .toList()
                    : List.of();

            ProductoDTO dto = new ProductoDTO(
                    p.getId(),
                    p.getTitle(),
                    p.getEstado(),
                    p.getPrice(),
                    p.getDescripcion(),
                    p.getFecha_publicacion() != null ? p.getFecha_publicacion().toString() : null,
                    p.getFecha_venta() != null ? p.getFecha_venta().toString() : null,
                    p.getVendido(),
                    p.getCategoria() != null ? p.getCategoria().getId() : null,
                    p.getCategoria() != null ? p.getCategoria().getName() : null,
                    p.getVendedor() != null ? p.getVendedor().getId() : null,
                    p.getComprador() != null ? p.getComprador().getId() : null,
                    imagenes
                    
            );

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
    
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @RequestPart("producto") ProductoDTO productoDTO,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes,
            @RequestPart(value = "imagenesEliminadas", required = false) String imagenesEliminadasJson) {

        try {
        	
            // Convertir el JSON recibido en lista de IDs
            List<Long> imagenesEliminadas = new ArrayList<>();
            if (imagenesEliminadasJson != null && !imagenesEliminadasJson.isEmpty()) {
                imagenesEliminadas = Arrays.stream(
                        imagenesEliminadasJson.replace("[", "").replace("]", "").split(","))
                        .filter(s -> !s.isBlank())
                        .map(Long::parseLong)
                        .toList();
            }

            // Llamar al service que maneja la lógica de actualización completa
            Producto productoActualizado = productoService.actualizarProductoConImagenes(id, productoDTO, imagenes, imagenesEliminadas);

            // Preparar respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Producto actualizado correctamente");

            Map<String, Object> productoResponse = new HashMap<>();
            productoResponse.put("id", productoActualizado.getId());
            productoResponse.put("title", productoActualizado.getTitle());
            productoResponse.put("descripcion", productoActualizado.getDescripcion());
            productoResponse.put("price", productoActualizado.getPrice());
            productoResponse.put("estado", productoActualizado.getEstado());
            productoResponse.put("vendido", productoActualizado.getEstado());
            productoResponse.put("categoriaId", productoActualizado.getCategoria().getId());
            productoResponse.put("vendedorId", productoActualizado.getVendedor().getId());
            productoResponse.put("imagenesUrls", productoActualizado.getListaImagenes().stream()
                    .map(img -> img.getUrl())
                    .toList());

            response.put("producto", productoResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar producto: " + e.getMessage());
        }
    }
    
    @GetMapping("/explorar")
    public ResponseEntity<List<ProductoDTO>> explorarProductos() {
        List<Producto> productos = productoService.getProductosExplorar();

        List<ProductoDTO> dtos = productos.stream().map(p -> {
            List<ImagenDTO> imagenes = p.getListaImagenes() != null ?
                p.getListaImagenes().stream()
                 .map(img -> new ImagenDTO(img.getId(), img.getUrl()))
                 .toList() :
                new ArrayList<>();

            return new ProductoDTO(
                    p.getId(),
                    p.getTitle(),
                    p.getEstado(),
                    p.getPrice(),
                    p.getDescripcion(),
                    p.getFecha_publicacion() != null ? p.getFecha_publicacion().toString() : null,
                    p.getFecha_venta() != null ? p.getFecha_venta().toString() : null,
                    p.getVendido(),
                    p.getCategoria() != null ? p.getCategoria().getId() : null,
                    p.getCategoria() != null ? p.getCategoria().getName() : null,
                    p.getVendedor() != null ? p.getVendedor().getId() : null,
                    p.getComprador() != null ? p.getComprador().getId() : null,
                    imagenes
            );
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    
    @PutMapping("/comprar/{idProducto}/{idComprador}")
    public ResponseEntity<?> comprarProducto(@PathVariable Long idProducto, @PathVariable Long idComprador) {
        try {
            Producto productoActualizado = productoService.marcarComoVendido(idProducto, idComprador);

            return ResponseEntity.ok(Map.of(
                    "message", "Compra realizada correctamente",
                    "productoId", productoActualizado.getId(),
                    "vendido", productoActualizado.getVendido(),
                    "compradorId", productoActualizado.getComprador().getId()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al realizar la compra: " + e.getMessage()));
        }
    }






}
