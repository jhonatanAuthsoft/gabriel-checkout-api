package com.projeto.modelo.controller.dto.request;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.model.entity.Endereco;
import com.projeto.modelo.model.enums.PermissaoStatus;
import com.projeto.modelo.util.StringUtils;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.http.HttpStatus;

public record CadastraUsuarioDTO(String email, String nome, @CPF String cpf, String celular, PermissaoStatus permissao,
                                 Endereco endereco) {
    public CadastraUsuarioDTO {
        if (StringUtils.isNullOrEmpty(email)) {
            throw new ExcecoesCustomizada("email não pode ficar em branco", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isNullOrEmpty(nome)) {
            throw new ExcecoesCustomizada("nome não pode ficar em branco", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isNullOrEmpty(permissao.toString())) {
            if (endereco == null) {
                throw new ExcecoesCustomizada("O endereço não pode ficar em branco", HttpStatus.BAD_REQUEST);
            }

            if (StringUtils.isNullOrEmpty(cpf)) {
                throw new ExcecoesCustomizada("O cpf não pode ficar em branco e deve ser válido", HttpStatus.BAD_REQUEST);
            }

            if (StringUtils.isNullOrEmpty(celular)) {
                throw new ExcecoesCustomizada("O celular não pode ficar em branco", HttpStatus.BAD_REQUEST);
            }
        }
    }
}

