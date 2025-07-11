package com.projeto.modelo.controller.dto.response.dashboard;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TotalVendas(
        BigDecimal valorVendido,
        Long totalVendasAtual,
        BigDecimal percentualPeriodoAnterior
) {
}
