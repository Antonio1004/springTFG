package com.marketplace.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Mensaje")
public class Mensaje {
	
 	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 	
 	@Column(name = "mensaje")
    private String mensaje;
 	
 	@Column(name = "fecha")
    private LocalDateTime fecha;
 	
 	@Column(name = "leido")
    private boolean leido;
 	
 	@ManyToOne
    @JoinColumn(name = "id_receptor")
    private User receptor;
	
	@ManyToOne
    @JoinColumn(name = "id_emisor")
    private User emisor;
	
	@ManyToOne
    @JoinColumn(name = "id_producto")
    private Producto producto;


	
	
	

	public Producto getProducto() {
		return producto;
	}

	public void setProducto(Producto producto) {
		this.producto = producto;
	}

	public Mensaje(Long id, String mensaje, LocalDateTime fecha, boolean leido, User receptor, User emisor,
			Producto producto) {
		super();
		this.id = id;
		this.mensaje = mensaje;
		this.fecha = fecha;
		this.leido = leido;
		this.receptor = receptor;
		this.emisor = emisor;
		this.producto = producto;
	}

	public Mensaje() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}

	public boolean isLeido() {
		return leido;
	}

	public void setLeido(boolean leido) {
		this.leido = leido;
	}

	public User getReceptor() {
		return receptor;
	}

	public void setReceptor(User receptor) {
		this.receptor = receptor;
	}

	public User getEmisor() {
		return emisor;
	}

	public void setEmisor(User emisor) {
		this.emisor = emisor;
	}
 	
 	

}
