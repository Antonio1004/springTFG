package com.marketplace.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.marketplace.model.Categoria;
import com.marketplace.model.ImageProducto;
import com.marketplace.model.Producto;
import com.marketplace.model.ProductoDTO;
import com.marketplace.model.User;
import com.marketplace.model.UserDTO;
import com.marketplace.model.VerificationCode;
import com.marketplace.repository.MensajeRepository;
import com.marketplace.repository.ProductoRepository;
import com.marketplace.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductoService {

	@Autowired
	ProductoRepository productoRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CategoriaService categoriaService;

	@Autowired
	UserService userService;
	
	@Autowired
	MensajeRepository mensajeRepository;

	@Autowired
	CloudinaryService cloudinaryService;

	@Autowired
	ImageProductoService imageProductoService;

	public List<Producto> getAllProductosByVendedorId(Long idVendedor) {
		return productoRepository.findAllByVendedorId(idVendedor);
	}

	// Registrar usuario nuevo
	public Producto saveProducto(ProductoDTO productoDTO) {

		// Obtener categoría y vendedor
		Categoria categoria = categoriaService.getCategoriaById(productoDTO.getCategoriaId());
		User vendedor = userService.getUserById(productoDTO.getVendedorId());

		// Crear el producto
		Producto producto = new Producto();
		producto.setTitle(productoDTO.getTitle());
		producto.setEstado(productoDTO.getEstado());
		producto.setCategoria(categoria);
		producto.setPrice(productoDTO.getPrice());
		producto.setDescripcion(productoDTO.getDescripcion());
		producto.setVendedor(vendedor);
		producto.setVendido("no");
		producto.setFecha_publicacion(LocalDateTime.now());

		productoRepository.save(producto);
		return producto;

	}
	public Producto marcarComoVendido(Long idProducto, Long idComprador) {
	    // Verificar existencia del producto
	    Producto producto = productoRepository.findById(idProducto)
	            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

	    // Verificar existencia del comprador
	    User comprador = userRepository.findById(idComprador)
	            .orElseThrow(() -> new RuntimeException("Comprador no encontrado"));

	    // Comprobar si ya estaba vendido
	    if ("sí".equalsIgnoreCase(producto.getVendido())) {
	        throw new RuntimeException("Este producto ya ha sido vendido");
	    }

	    // Marcar como vendido y asignar comprador
	    producto.setVendido("sí");
	    producto.setComprador(comprador);
	    producto.setFecha_venta(LocalDateTime.now());

	    // Guardar cambios
	    return productoRepository.save(producto);
	}

	public Producto getProductoById(Long id) {
		return productoRepository.findById(id).orElse(null);
	}

	public Producto guardarProductoConImagenes(ProductoDTO productoDTO, List<MultipartFile> imagenes)
			throws IOException {
		// Guardar producto primero
		Producto productoGuardado = saveProducto(productoDTO);

		// Subir imágenes a Cloudinary y guardar en tabla ImageProducto
		List<ImageProducto> listaImagenes = new ArrayList<>();
		for (MultipartFile file : imagenes) {
			if (file != null && !file.isEmpty()) {
				Map uploadResult = cloudinaryService.upload(file);
				String url = uploadResult.get("secure_url").toString();

				ImageProducto img = new ImageProducto();
				img.setUrl(url);
				img.setProducto(productoGuardado);
				imageProductoService.saveImageProducto(img);

				listaImagenes.add(img);
			}
		}

		// Asociar imágenes al producto y actualizarlo
		productoGuardado.setListaImagenes(listaImagenes);
		return productoRepository.save(productoGuardado);
	}

	public Producto actualizarProductoConImagenes(Long id, ProductoDTO productoDTO, List<MultipartFile> imagenes,
			List<Long> imagenesEliminadas) throws IOException {
		Producto productoExistente = getProductoById(id);
		if (productoExistente == null) {
			throw new RuntimeException("Producto no encontrado");
		}

// Actualizar campos básicos
		productoExistente.setTitle(productoDTO.getTitle());
		productoExistente.setDescripcion(productoDTO.getDescripcion());
		productoExistente.setPrice(productoDTO.getPrice());
		productoExistente.setEstado(productoDTO.getEstado());
		productoExistente.setVendido(productoDTO.getVendido());

// Actualizar categoría si se cambia
		if (productoDTO.getCategoriaId() != null) {
			productoExistente.setCategoria(categoriaService.getCategoriaById(productoDTO.getCategoriaId()));
		}

//  Eliminar imágenes seleccionadas
		if (imagenesEliminadas != null && !imagenesEliminadas.isEmpty()) {
			productoExistente.getListaImagenes().removeIf(img -> imagenesEliminadas.contains(img.getId()));
			for (Long idImg : imagenesEliminadas) {
				 try {
			            imageProductoService.deleteById(idImg);
			        } catch (Exception e) {
			            System.out.println("No se pudo borrar imagen con id " + idImg + ": " + e.getMessage());
			            // continuar con la siguiente imagen
			        }// Opcional: eliminar también de Cloudinary si guardas el public_id
// cloudinaryService.delete(publicId);
			}
		}

// Manejo de nuevas imágenes
		if (imagenes != null && !imagenes.isEmpty()) {
			List<ImageProducto> nuevasImagenes = new ArrayList<>();
			for (MultipartFile file : imagenes) {
				if (!file.isEmpty()) {
					Map uploadResult = cloudinaryService.upload(file);
					String url = uploadResult.get("secure_url").toString();

					ImageProducto img = new ImageProducto();
					img.setUrl(url);
					img.setProducto(productoExistente);
					imageProductoService.saveImageProducto(img);

					nuevasImagenes.add(img);
				}
			}

// Añadir las nuevas imágenes a la lista existente
			if (productoExistente.getListaImagenes() == null) {
				productoExistente.setListaImagenes(nuevasImagenes);
			} else {
				productoExistente.getListaImagenes().addAll(nuevasImagenes);
			}
		}

		return productoRepository.save(productoExistente);
	}
	
	public List<Producto> getProductosExplorar() {
	    // Ejemplo simple: traer todos los productos que NO están vendidos
		return productoRepository.findAllByVendido("no");

	}
	@Transactional
	public boolean eliminarProducto(Long id) {
	    // Primero verificamos si el producto existe
	    Optional<Producto> productoOpt = productoRepository.findById(id);
	    if (productoOpt.isEmpty()) {
	        return false; // no existe
	    }

	    // Borrar mensajes asociados al producto
	    mensajeRepository.deleteByProductoId(id);

	    // Borrar el producto (las imágenes se borran en cascada)
	    productoRepository.deleteById(id);

	    return true; // se borró correctamente
	}
	public List<Producto> getAllProductos() {
	    return productoRepository.findAll();
	}

	
	public List<Producto> getAllComprasByCompradorId(Long compradorId) {
	    return productoRepository.findAllByCompradorId(compradorId);
	}

}
