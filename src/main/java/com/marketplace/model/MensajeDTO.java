package com.marketplace.model;

import java.time.LocalDateTime;

public class MensajeDTO {

    private Long idEmisor;
    private Long idReceptor;
    private Long productoId;
    private String nombreProducto;
    private String mensaje;
    private LocalDateTime fecha;
    private boolean leido;

    // 🔹 Constructor vacío (requerido por Spring)
    public MensajeDTO() {}

    
	public MensajeDTO(Long idEmisor, Long idReceptor, String mensaje, LocalDateTime fecha, boolean leido) {
		super();
		this.idEmisor = idEmisor;
		this.idReceptor = idReceptor;
		this.mensaje = mensaje;
		this.fecha = fecha;
		this.leido = leido;
	}


	

	public MensajeDTO(Long idEmisor, Long idReceptor, Long productoId, String nombreProducto, String mensaje,
			LocalDateTime fecha, boolean leido) {
		super();
		this.idEmisor = idEmisor;
		this.idReceptor = idReceptor;
		this.productoId = productoId;
		this.nombreProducto = nombreProducto;
		this.mensaje = mensaje;
		this.fecha = fecha;
		this.leido = leido;
	}


	public Long getProductoId() {
		return productoId;
	}


	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}


	public String getNombreProducto() {
		return nombreProducto;
	}


	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}


	public Long getIdEmisor() {
		return idEmisor;
	}


	public void setIdEmisor(Long idEmisor) {
		this.idEmisor = idEmisor;
	}


	public Long getIdReceptor() {
		return idReceptor;
	}


	public void setIdReceptor(Long idReceptor) {
		this.idReceptor = idReceptor;
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

	
}