package com.marketplace.model;


public class UserDTO {
    private String name;
 	
    private String email;
 	
    private String password;
 	
    private String direccion;
 	
    private String foto;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public UserDTO(String name, String email, String password, String direccion, String foto) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
		this.direccion = direccion;
		this.foto = foto;
	}

	public UserDTO() {
		super();
	}
    
}
