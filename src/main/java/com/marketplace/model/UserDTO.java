package com.marketplace.model;

import java.time.LocalDateTime;

public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String direccion;
    private String foto;

    private String tipo_bloqueo;
    private LocalDateTime fecha_fin;

    private String role; 

    public UserDTO() {
        super();
    }

    public UserDTO(Long id, String name, String email, String password, String direccion,
                   String foto, String tipo_bloqueo, LocalDateTime fecha_fin, String role) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.direccion = direccion;
        this.foto = foto;
        this.tipo_bloqueo = tipo_bloqueo;
        this.fecha_fin = fecha_fin;
        this.role = role; // ← NUEVO
    }

    // ------------------ GETTERS Y SETTERS ------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public String getTipo_bloqueo() { return tipo_bloqueo; }
    public void setTipo_bloqueo(String tipo_bloqueo) { this.tipo_bloqueo = tipo_bloqueo; }

    public LocalDateTime getFecha_fin() { return fecha_fin; }
    public void setFecha_fin(LocalDateTime fecha_fin) { this.fecha_fin = fecha_fin; }

    public String getRole() { return role; }       
    public void setRole(String role) { this.role = role; }
}
