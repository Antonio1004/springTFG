package com.marketplace.model;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Valoracion")
public class Valoracion {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 	
 	@Column(name = "estrellas")
    private Integer stars;
 	
 	@Column(name = "comentario")
    private String comment;
 	
 	@Column(name = "fecha")
    private LocalDate date;
 	
 	@ManyToOne
    @JoinColumn(name = "id_comprador")
    private User comprador;
 	
 	@ManyToOne
    @JoinColumn(name = "id_vendedor")
    private User vendedor;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	public Integer getStars() {
		return stars;
	}

	public void setStars(Integer stars) {
		this.stars = stars;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Valoracion other = (Valoracion) obj;
		return Objects.equals(id, other.id);
	}

	

	public Valoracion(Long id, Integer stars, String comment, LocalDate date, User comprador, User vendedor) {
		super();
		this.id = id;
		this.stars = stars;
		this.comment = comment;
		this.date = date;
		this.comprador = comprador;
		this.vendedor = vendedor;
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

	public Valoracion() {
		super();
	}
 	
 	
 	
	
}
