package com.marketplace.model;

public class ValoracionDTO {

    private Integer stars;
    private String comment;
    private Long idComprador;
    private Long idVendedor;

    public ValoracionDTO() {
    }

    public ValoracionDTO(Integer stars, String comment, Long idComprador, Long idVendedor) {
        this.stars = stars;
        this.comment = comment;
        this.idComprador = idComprador;
        this.idVendedor = idVendedor;
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

    public Long getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(Long idComprador) {
        this.idComprador = idComprador;
    }

    public Long getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(Long idVendedor) {
        this.idVendedor = idVendedor;
    }
}
