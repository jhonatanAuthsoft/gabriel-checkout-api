package com.projeto.modelo.controller.dto.request;

import com.projeto.modelo.model.enums.BandeiraCartao;
import lombok.Builder;

@Builder
public record DadosCartao(
        String numeroCartao,
        String nomeImpresso,
        String dataVencimento,
        String codigoSeguranca,
        BandeiraCartao bandeiraCartao,
        Integer parcelas
) {
}
