package com.projeto.modelo.controller.dto.response.dashboard;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record TicketMedio(
        BigDecimal ticketMedio,
        BigDecimal percentualPeriodoAnterior
) {
}
