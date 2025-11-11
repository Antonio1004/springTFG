package com.marketplace.model;

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
    private String fecha;
 	
 	@Column(name = "leido")
    private boolean leido;
 	
 	@ManyToOne
    @JoinColumn(name = "id_receptor")
    private User receptor;
	
	@ManyToOne
    @JoinColumn(name = "id_emisor")
    private User emisor;

	public Mensaje(Long id, String mensaje, String fecha, boolean leido, User receptor, User emisor) {
		super();
		this.id = id;
		this.mensaje = mensaje;
		this.fecha = fecha;
		this.leido = leido;
		this.receptor = receptor;
		this.emisor = emisor;
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

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
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
