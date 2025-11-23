package com.marketplace.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="Producto")
public class Producto {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "title")
    private String title;

	@Column(name = "price")
    private BigDecimal price;
	
	@Column(name = "estado")
    private String estado;
	
	@Column(name = "descripcion")
    private String descripcion;
	
	@Column(name = "fecha_publicacion")
    private LocalDateTime fecha_publicacion;
	
	@Column(name = "fecha_venta")
    private LocalDateTime fecha_venta;
	
	@ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
	
	@ManyToOne
    @JoinColumn(name = "id_comprador")
    private User comprador;
	
	@ManyToOne
    @JoinColumn(name = "id_vendedor")
    private User vendedor;
	
	@Column(name = "vendido", nullable = false, length = 2)
	private String vendido = "no";
	
	
	@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ImageProducto>listaImagenes;
	
	@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Mensaje>listaMensajes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public LocalDateTime getFecha_publicacion() {
		return fecha_publicacion;
	}

	public void setFecha_publicacion(LocalDateTime fecha_publicacion) {
		this.fecha_publicacion = fecha_publicacion;
	}

	public LocalDateTime getFecha_venta() {
		return fecha_venta;
	}

	public void setFecha_venta(LocalDateTime localDateTime) {
		this.fecha_venta = localDateTime;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public User getComprador() {
		return comprador;
	}

	public void setComprador(User comprador) {
		this.comprador = comprador;
	}

	public User getVendedor() {
		return vendedor;
	}

	public void setVendedor(User vendedor) {
		this.vendedor = vendedor;
	}

	public List<ImageProducto> getListaImagenes() {
		return listaImagenes;
	}

	public void setListaImagenes(List<ImageProducto> listaImagenes) {
		this.listaImagenes = listaImagenes;
	}

	

	public String getVendido() {
		return vendido;
	}

	public void setVendido(String vendido) {
		this.vendido = vendido;
	}

	public Producto(Long id, String title, BigDecimal price, String estado, String descripcion,
			LocalDateTime fecha_publicacion, LocalDateTime fecha_venta, Categoria categoria, User comprador, User vendedor,
			String vendido, List<ImageProducto> listaImagenes) {
		super();
		this.id = id;
		this.title = title;
		this.price = price;
		this.estado = estado;
		this.descripcion = descripcion;
		this.fecha_publicacion = fecha_publicacion;
		this.fecha_venta = fecha_venta;
		this.categoria = categoria;
		this.comprador = comprador;
		this.vendedor = vendedor;
		this.vendido = vendido;
		this.listaImagenes = listaImagenes;
	}

	public Producto() {
		super();
	}

	
	
}
