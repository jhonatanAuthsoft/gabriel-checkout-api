package com.projeto.modelo.controller.dto.request;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import org.springframework.http.HttpStatus;

public record UsuarioEsqueceuSenhaRequestDTO(String email) {
    public UsuarioEsqueceuSenhaRequestDTO {
        if (email == null || email.isBlank()) {
            throw new ExcecoesCustomizada("Email não pode ser nulo ou vazio", HttpStatus.BAD_REQUEST);
        }
    }
}
