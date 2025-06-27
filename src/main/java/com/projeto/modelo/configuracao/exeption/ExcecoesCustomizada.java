package com.projeto.modelo.configuracao.exeption;

import org.springframework.http.HttpStatus;

public class ExcecoesCustomizada extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private final HttpStatus httpStatus;

    public ExcecoesCustomizada(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
