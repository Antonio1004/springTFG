package com.marketplace.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ProductoDTO {

    private Long id;
    private String title;
    private String estado;
    private BigDecimal price;
    private String descripcion;
    private String fecha_publicacion;
    private String fecha_venta;
    private String vendido;



    private Long categoriaId;
    private String categoriaNombre; // opcional
    private Long vendedorId;   // ID del usuario que publica
    private Long compradorId;  // ID del comprador, puede ser null
    
    private List<ImagenDTO> imagenes; // solo id + url

    public ProductoDTO() {}

   
    public ProductoDTO(Long id, String title, String estado, BigDecimal price, String descripcion,
			String fecha_publicacion, String fecha_venta, String vendido, Long categoriaId, String categoriaNombre,
			Long vendedorId, Long compradorId, List<ImagenDTO> imagenes) {
		super();
		this.id = id;
		this.title = title;
		this.estado = estado;
		this.price = price;
		this.descripcion = descripcion;
		this.fecha_publicacion = fecha_publicacion;
		this.fecha_venta = fecha_venta;
		this.vendido = vendido;
		this.categoriaId = categoriaId;
		this.categoriaNombre = categoriaNombre;
		this.vendedorId = vendedorId;
		this.compradorId = compradorId;
		this.imagenes = imagenes;
	}


	// Getters y setters
    public String getVendido() { return vendido; }
    public void setVendido(String vendido) { this.vendido = vendido; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFecha_publicacion() { return fecha_publicacion; }
    public void setFecha_publicacion(String fecha_publicacion) { this.fecha_publicacion = fecha_publicacion; }

    public String getFecha_venta() { return fecha_venta; }
    public void setFecha_venta(String fecha_venta) { this.fecha_venta = fecha_venta; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public String getCategoriaNombre() { return categoriaNombre; }
    public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

    public Long getVendedorId() { return vendedorId; }
    public void setVendedorId(Long vendedorId) { this.vendedorId = vendedorId; }

    public Long getCompradorId() { return compradorId; }
    public void setCompradorId(Long compradorId) { this.compradorId = compradorId; }

    public List<ImagenDTO> getImagenes() { return imagenes; }
    public void setImagenes(List<ImagenDTO> imagenes) { this.imagenes = imagenes; }
}
