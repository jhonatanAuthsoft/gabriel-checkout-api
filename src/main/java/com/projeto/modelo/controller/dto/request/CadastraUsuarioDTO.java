package com.projeto.modelo.controller.dto.request;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.util.StringUtils;
import org.springframework.http.HttpStatus;

public record CadastraUsuarioDTO(String email, String nome) {
    public CadastraUsuarioDTO {
        if (StringUtils.isNullOrEmpty(email)) {
            throw new ExcecoesCustomizada("email não pode ficar em branco", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isNullOrEmpty(nome)) {
            throw new ExcecoesCustomizada("nome não pode ficar em branco", HttpStatus.BAD_REQUEST);
        }
    }
}

