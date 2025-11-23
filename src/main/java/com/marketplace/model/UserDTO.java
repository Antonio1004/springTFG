package com.marketplace.model;


public class UserDTO {
	private Long id;
   

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
	 public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
	

	public UserDTO(Long id, String name, String email, String password, String direccion, String foto) {
			super();
			this.id = id;
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
