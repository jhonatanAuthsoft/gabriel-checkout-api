package com.projeto.modelo.controller.dto.request;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import org.springframework.http.HttpStatus;

import java.util.List;

public record CriarVendaRequestDTO(
        List<Long> idsProduto,
        List<Long> idsPlano,
        Long idCliente,
        Long idVendedor,
        String codigoCupom
) {
    public CriarVendaRequestDTO {
        if (idCliente == null || idCliente == 0) {
            throw new ExcecoesCustomizada("O id do cliente não pode ficar em branco e deve ser válido!", HttpStatus.BAD_REQUEST);
        }

        if (idsProduto == null || idsProduto.isEmpty() || idsProduto.contains(null) || idsProduto.contains(0L)) {
            throw new ExcecoesCustomizada("A lista de produtos não pode estar vazia e deve conter apenas IDs válidos!", HttpStatus.BAD_REQUEST);
        }

        if (idsPlano == null || idsPlano.isEmpty() || idsPlano.contains(null) || idsPlano.contains(0L)) {
            throw new ExcecoesCustomizada("A lista de planos não pode estar vazia e deve conter apenas IDs válidos!", HttpStatus.BAD_REQUEST);
        }
    }
}
