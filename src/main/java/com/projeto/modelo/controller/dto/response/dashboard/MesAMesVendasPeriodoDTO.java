package com.projeto.modelo.controller.dto.response.dashboard;

import com.projeto.modelo.model.enums.Mes;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MesAMesVendasPeriodoDTO(
        Mes mes,
        BigDecimal totalVenda,
        BigDecimal totalCampanha,
        BigDecimal totalLink
) {
}
