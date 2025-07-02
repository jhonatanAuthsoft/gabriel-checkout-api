package com.projeto.modelo.model.entity.produto;

import jakarta.persistence.Embedded;
import lombok.Builder;

@Builder
public record DadosProduto(
        @Embedded
        DadosGerais dadosGerais,

        @Embedded
        FormatoCategoria formatoCategoria,

        @Embedded
        Cobranca cobranca,

        @Embedded
        Disponibilidade disponibilidade,

        @Embedded
        SuporteGarantia suporteGarantia,
        String urlPersonalizada
) {
}