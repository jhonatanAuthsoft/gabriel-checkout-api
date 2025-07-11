package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.response.dashboard.ChargebackDTO;
import com.projeto.modelo.controller.dto.response.dashboard.DashboardResponseDTO;
import com.projeto.modelo.controller.dto.response.dashboard.TotalVendasPorPeriodoDTO;

import java.time.LocalDate;

public interface DashboardService {
    DashboardResponseDTO carregarDashboard(LocalDate dataInicial, LocalDate dataFim);

    TotalVendasPorPeriodoDTO carregarVendaPeriodo(LocalDate dataInicial, LocalDate dataFim);

    ChargebackDTO carregarChargeback(LocalDate dataInicial, LocalDate dataFim);
}
