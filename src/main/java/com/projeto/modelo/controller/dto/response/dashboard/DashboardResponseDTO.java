package com.projeto.modelo.controller.dto.response.dashboard;

import com.projeto.modelo.model.enums.Mes;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record DashboardResponseDTO(
    LocalDate periodoCalculadoInicio,
    LocalDate periodoCalculadoFim,
    TotalVendas totalVendas,
    TicketMedio ticketMedio,
    Churn churn,
    List<ProdutosMaisVendidos> produtosMaisVendidos,
    Reembolsos30Dias reembolsos30Dias,
    Upsell upsell,
    ChargebackDTO chargebackDTO,
    TotalVendasPorPeriodoDTO totalVendasPorPeriodoDTO

) {
}
