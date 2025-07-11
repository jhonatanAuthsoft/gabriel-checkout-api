package com.projeto.modelo.controller.dto.response.dashboard;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Upsell(
        BigDecimal percentualUpsellPeriodoAtual,
        BigDecimal percentualPeriodoAnterior
) {
}
