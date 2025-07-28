package com.projeto.modelo.configuracao.exeption;

import com.projeto.modelo.controller.dto.response.ErroResponseDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ExcecoesCustomizada.class)
    public ResponseEntity<Object> handleCustomException(ExcecoesCustomizada ex) {
        ErroResponseDTO errorResponse = new ErroResponseDTO(ex.getMessage());
        return new ResponseEntity<>(errorResponse, ex.getHttpStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        // Para simplificação, vamos pegar apenas a primeira violação
        ConstraintViolation<?> violation = ex.getConstraintViolations().iterator().next();

        String campo = "";
        if (violation.getPropertyPath() != null) {
            campo = violation.getPropertyPath().toString();
        }

        String mensagem = violation.getMessage(); // ou getMessageTemplate() se quiser o template

        // Você pode criar um DTO personalizado se quiser
        Map<String, String> erro = new HashMap<>();
        erro.put("campo", campo);
        erro.put("problema", mensagem);

        return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String campo = extrairCampoDuplicado(ex);
        String mensagem = "Já está cadastrado em nosso banco de dados!";
        Map<String, String> erro = new HashMap<>();
        erro.put("campo", campo);
        erro.put("problema", mensagem);
        return new ResponseEntity<>(erro, HttpStatus.CONFLICT);
    }

    private String extrairCampoDuplicado(DataIntegrityViolationException ex) {
        String rootMessage = ex.getRootCause() != null ? ex.getRootCause().getMessage() : "";

        // Regex para extrair o nome do campo entre parênteses após "Key ("
        Pattern pattern = Pattern.compile("Key \\((.*?)\\)=");
        Matcher matcher = pattern.matcher(rootMessage);
        if (matcher.find()) {
            return matcher.group(1); // ex: cpf, email, etc.
        }

        return "informado"; // fallback
    }
}
