package com.marketplace.model;

public class ConversacionResumenDTO {

    private Long idUsuario;
    private Long idProducto;
    private String nombreUsuario;
    private String nombreProducto;
	public ConversacionResumenDTO(Long idUsuario, Long idProducto, String nombreUsuario, String nombreProducto) {
		super();
		this.idUsuario = idUsuario;
		this.idProducto = idProducto;
		this.nombreUsuario = nombreUsuario;
		this.nombreProducto = nombreProducto;
	}
	public Long getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}
	public Long getIdProducto() {
		return idProducto;
	}
	public void setIdProducto(Long idProducto) {
		this.idProducto = idProducto;
	}
	public String getNombreUsuario() {
		return nombreUsuario;
	}
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
	public String getNombreProducto() {
		return nombreProducto;
	}
	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

    
}
