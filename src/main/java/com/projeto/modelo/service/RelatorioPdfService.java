package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.RelatorioVendasDTO;
import com.projeto.modelo.controller.dto.response.dashboard.DashboardResponseDTO;

import java.time.LocalDate;

public interface RelatorioPdfService {

    byte[] gerarRelatorioDashboard(DashboardResponseDTO dashboard);

    byte[] gerarRelatorioVendasSelecionadas(RelatorioVendasDTO solicitacao);

    byte[] gerarRelatorioClientes(LocalDate dataInicial, LocalDate dataFim);
}
