package com.marketplace.exception;

public class DataBaseErrorException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public DataBaseErrorException() {
        super("Error en base de datos");
    }

}