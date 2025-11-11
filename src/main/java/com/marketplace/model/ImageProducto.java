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
@Table(name = "image_producto")
public class ImageProducto {
	  	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	  	
	  	@Column(name = "imagen", nullable = false, length = 500)
	    private String url;

	  	@ManyToOne
	    @JoinColumn(name = "producto_id")
	    private Producto producto;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public Producto getProducto() {
			return producto;
		}

		public void setProducto(Producto producto) {
			this.producto = producto;
		}

		public ImageProducto(Long id, String url, Producto producto) {
			super();
			this.id = id;
			this.url = url;
			this.producto = producto;
		}

		public ImageProducto() {
			super();
		}
	  	
	  	
}
