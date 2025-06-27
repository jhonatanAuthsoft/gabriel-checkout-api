package com.projeto.modelo.configuracao.exeption;

import com.projeto.modelo.controller.dto.response.ErroResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ExcecoesCustomizada.class)
    public ResponseEntity<Object> handleCustomException(ExcecoesCustomizada ex) {
        ErroResponseDTO errorResponse = new ErroResponseDTO(ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }
}
