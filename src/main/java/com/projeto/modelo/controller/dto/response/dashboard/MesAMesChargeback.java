package com.projeto.modelo.controller.dto.response.dashboard;

import com.projeto.modelo.model.enums.Mes;
import lombok.Builder;

@Builder
public record MesAMesChargeback(
        Mes mes,
        Long total
) {}
