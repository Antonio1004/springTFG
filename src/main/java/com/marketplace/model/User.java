package com.marketplace.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="Usuario")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "estado_cuenta")
    private String estado_cuenta = "pendiente";

    @Column(name = "direccion")
    private String direccion;

    @Column(name = "foto")
    private String foto;

    // 🔥 NUEVOS CAMPOS
    @Column(name = "tipo_bloqueo")
    private String tipo_bloqueo = "ninguno"; // ninguno | temporal | permanente

    @Column(name = "fecha_fin")
    private LocalDateTime fecha_fin; // puede ser null


    @OneToMany(mappedBy = "comprador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Valoracion> valoracionesHechas;

    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Valoracion> valoracionesRecibidas;

    @OneToMany(mappedBy = "comprador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productosComprados;

    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productosPublicados;

    @OneToMany(mappedBy = "emisor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mensaje> mensajesEnviados;

    @OneToMany(mappedBy = "receptor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mensaje> mensajesRecibidos;



    public User() {
        super();
    }

    public User(Long id, String name, String email, String password, String role, String estado_cuenta,
                String direccion, String foto, String tipo_bloqueo, LocalDateTime fecha_fin) {
        super();
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.estado_cuenta = estado_cuenta;
        this.direccion = direccion;
        this.foto = foto;
        this.tipo_bloqueo = tipo_bloqueo;
        this.fecha_fin = fecha_fin;
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

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEstado_cuenta() { return estado_cuenta; }
    public void setEstado_cuenta(String estado_cuenta) { this.estado_cuenta = estado_cuenta; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public String getTipo_bloqueo() { return tipo_bloqueo; }
    public void setTipo_bloqueo(String tipo_bloqueo) { this.tipo_bloqueo = tipo_bloqueo; }

    public LocalDateTime getFecha_fin() { return fecha_fin; }
    public void setFecha_fin(LocalDateTime fecha_fin) { this.fecha_fin = fecha_fin; }

    public List<Valoracion> getValoracionesHechas() { return valoracionesHechas; }
    public void setValoracionesHechas(List<Valoracion> valoracionesHechas) { this.valoracionesHechas = valoracionesHechas; }

    public List<Valoracion> getValoracionesRecibidas() { return valoracionesRecibidas; }
    public void setValoracionesRecibidas(List<Valoracion> valoracionesRecibidas) { this.valoracionesRecibidas = valoracionesRecibidas; }

    public List<Producto> getProductosComprados() { return productosComprados; }
    public void setProductosComprados(List<Producto> productosComprados) { this.productosComprados = productosComprados; }

    public List<Producto> getProductosPublicados() { return productosPublicados; }
    public void setProductosPublicados(List<Producto> productosPublicados) { this.productosPublicados = productosPublicados; }

    public List<Mensaje> getMensajesEnviados() { return mensajesEnviados; }
    public void setMensajesEnviados(List<Mensaje> mensajesEnviados) { this.mensajesEnviados = mensajesEnviados; }

    public List<Mensaje> getMensajesRecibidos() { return mensajesRecibidos; }
    public void setMensajesRecibidos(List<Mensaje> mensajesRecibidos) { this.mensajesRecibidos = mensajesRecibidos; }



    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        User other = (User) obj;
        return Objects.equals(email, other.email);
    }
}
