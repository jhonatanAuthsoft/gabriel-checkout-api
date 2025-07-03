package com.projeto.modelo.controller.dto.response;

import com.projeto.modelo.model.entity.Cupom;
import com.projeto.modelo.model.entity.Produto;
import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.model.enums.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record VendaResponseDTO(
        Long id,
        Produto produto,
        BigDecimal valorPago,
        String txid,
        String codigoSolicitacao,
        Cupom cupomUsado,
        OrigemCompra origemCompra,
        MetodoPagamento metodoPagamento,
        StatusPagamento statusPagamento,
        StatusVenda statusVenda,
        TipoCobranca tipoRecorrencia,
        Usuario cliente,
        Usuario vendedor,
        LocalDateTime dataCompra,
        LocalDateTime dataPagamento,
        LocalDateTime dataAtualizacao
) {
}
