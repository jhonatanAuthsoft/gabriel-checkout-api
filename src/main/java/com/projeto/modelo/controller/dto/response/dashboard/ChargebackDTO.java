package com.projeto.modelo.controller.dto.response.dashboard;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record ChargebackDTO(
        Integer totalChargeback,
        LocalDate periodoCalculadoInicio,
        LocalDate periodoCalculadoFim,
        List<MesAMesChargeback> mesAmes

) {
}
