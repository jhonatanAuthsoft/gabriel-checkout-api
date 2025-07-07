package com.projeto.modelo.controller.dto.request.cielo;

import lombok.Builder;

@Builder
public record CieloReceberPagamentoCartao(
        String merchantOrderId,
        CieloProprietario proprietario,
        CieloPagamento pagamento
) {
}
