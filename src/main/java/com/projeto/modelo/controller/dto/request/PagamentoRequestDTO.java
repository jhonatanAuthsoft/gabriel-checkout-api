package com.projeto.modelo.controller.dto.request;


import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.model.enums.MetodoPagamento;
import com.projeto.modelo.util.StringUtils;
import jakarta.validation.constraints.Email;
import org.springframework.http.HttpStatus;

public record PagamentoRequestDTO(
        MetodoPagamento tipoCobranca,
        @Email
        String email,
        Long idVenda
) {
    public PagamentoRequestDTO {
        if (idVenda == null || idVenda == 0) {
            throw new ExcecoesCustomizada("O id do produto não pode ficar em branco e deve ser válido!", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isNullOrEmpty(email)) {
            throw new ExcecoesCustomizada("O email não pode ficar em branco!", HttpStatus.BAD_REQUEST);
        }

        if (tipoCobranca != null && StringUtils.isNullOrEmpty(tipoCobranca.toString())) {
            throw new ExcecoesCustomizada("O tipo de cobrança não pode ficar em branco!", HttpStatus.BAD_REQUEST);
        }
    }
}
