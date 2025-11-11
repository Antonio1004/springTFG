package com.marketplace.model;

public class MensajeDTO {

    private Long idComprador;
    private Long idVendedor;
    private String mensaje;

    // 🔹 Constructor vacío (requerido por Spring)
    public MensajeDTO() {}

    // 🔹 Constructor completo (opcional)
    public MensajeDTO(Long idComprador, Long idVendedor, String mensaje) {
        this.idComprador = idComprador;
        this.idVendedor = idVendedor;
        this.mensaje = mensaje;
    }

    // 🔹 Getters y Setters
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

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}