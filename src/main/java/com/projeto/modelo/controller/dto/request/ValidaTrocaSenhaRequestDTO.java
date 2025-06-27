package com.projeto.modelo.controller.dto.request;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import org.springframework.http.HttpStatus;

public record ValidaTrocaSenhaRequestDTO(String email, String senhaNova, Integer codigo) {
    public ValidaTrocaSenhaRequestDTO {
        if (email == null || email.isBlank()) {
            throw new ExcecoesCustomizada("Email não pode ser nulo ou vazio", HttpStatus.BAD_REQUEST);
        }
        if (senhaNova == null || senhaNova.isBlank()) {
            throw new ExcecoesCustomizada("Senha não pode ser nulo ou vazio", HttpStatus.BAD_REQUEST);
        }
        if (codigo == null || String.valueOf(codigo).length() != 4) {
            throw new ExcecoesCustomizada("Codigo invalido", HttpStatus.BAD_REQUEST);
        }
    }
}
