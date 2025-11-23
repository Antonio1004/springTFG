package com.marketplace.model;

public class MediaValoracionDTO {
    private Double mediaEstrellas;
    private Long totalValoraciones;

    public MediaValoracionDTO(Double mediaEstrellas, Long totalValoraciones) {
        this.mediaEstrellas = mediaEstrellas;
        this.totalValoraciones = totalValoraciones;
    }

    public Double getMediaEstrellas() {
        return mediaEstrellas;
    }

    public void setMediaEstrellas(Double mediaEstrellas) {
        this.mediaEstrellas = mediaEstrellas;
    }

    public Long getTotalValoraciones() {
        return totalValoraciones;
    }

    public void setTotalValoraciones(Long totalValoraciones) {
        this.totalValoraciones = totalValoraciones;
    }
}
