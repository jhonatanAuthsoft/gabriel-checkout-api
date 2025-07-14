package com.projeto.modelo.controller.dto.response.dashboard;

import com.projeto.modelo.model.enums.Mes;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MesAMesChargebackQuery(
        BigDecimal mes,
        Long total
) {
    public Mes mesEnum() {
        return Mes.fromNumber(mes.intValue());
    }
}
