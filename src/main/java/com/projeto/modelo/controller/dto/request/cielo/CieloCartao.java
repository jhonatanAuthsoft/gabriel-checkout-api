package com.projeto.modelo.controller.dto.request.cielo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.projeto.modelo.model.enums.BandeiraCartao;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CieloCartao(
        String numeroCartao,
        String nomeImpresso,
        String dataVencimento,
        String codigoSeguranca,
        BandeiraCartao bandeiraCartao
) {
}
