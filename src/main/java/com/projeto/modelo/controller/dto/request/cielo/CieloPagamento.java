package com.projeto.modelo.controller.dto.request.cielo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.projeto.modelo.model.enums.TipoCartao;
import com.projeto.modelo.model.enums.TipoParcelamento;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CieloPagamento(
        TipoCartao tipoCartao,
        BigDecimal valor,
        String moeda,
        String pais,
        String softDescriptor,
        Integer parcelas,
        TipoParcelamento tipoParcelamento,
        Boolean capture,
        CieloCartao cieloCartao
) {
}
