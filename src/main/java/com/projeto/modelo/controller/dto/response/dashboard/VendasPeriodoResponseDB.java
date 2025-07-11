package com.projeto.modelo.controller.dto.response.dashboard;

import java.math.BigDecimal;

public record VendasPeriodoResponseDB(
        BigDecimal totalValorPeriodoAtual,
        BigDecimal totalValorPeriodoAnterior,
        Long vendasTotaisPeriodoAtual,
        Long vendasTotaisPeriodoAnterior
) {}
