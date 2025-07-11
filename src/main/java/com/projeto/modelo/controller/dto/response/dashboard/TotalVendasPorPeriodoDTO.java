package com.projeto.modelo.controller.dto.response.dashboard;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record TotalVendasPorPeriodoDTO(
        LocalDate periodoCalculadoInicio,
        LocalDate periodoCalculadoFim,
        List<MesAMesVendasPeriodoDTO> mesAmes
) {
}
