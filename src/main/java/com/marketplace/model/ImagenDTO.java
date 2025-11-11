package com.marketplace.model;

public class ImagenDTO {
	 private Long id;
	    private String url;
		public ImagenDTO(Long id, String url) {
			super();
			this.id = id;
			this.url = url;
		}
		public ImagenDTO() {
			super();
		}
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
	    
}
