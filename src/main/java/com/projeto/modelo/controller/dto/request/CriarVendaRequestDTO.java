package com.projeto.modelo.controller.dto.request;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import org.springframework.http.HttpStatus;

public record CriarVendaRequestDTO(
        Long idProduto,
        Long idPlano,
        Long idCliente,
        Long idVendedor,
        String codigoCupom
) {
    public CriarVendaRequestDTO {
        if (idCliente == null || idCliente == 0) {
            throw new ExcecoesCustomizada("O id do produto não pode ficar em branco e deve ser válido!", HttpStatus.BAD_REQUEST);
        }

        if (idProduto == null || idProduto == 0) {
            throw new ExcecoesCustomizada("O id do produto não pode ficar em branco e deve ser válido!", HttpStatus.BAD_REQUEST);
        }

        if (idPlano == null || idPlano == 0) {
            throw new ExcecoesCustomizada("O id do produto não pode ficar em branco e deve ser válido!", HttpStatus.BAD_REQUEST);
        }
    }
}
