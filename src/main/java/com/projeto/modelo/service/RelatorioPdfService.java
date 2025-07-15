package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.RelatorioVendasDTO;
import com.projeto.modelo.controller.dto.response.dashboard.DashboardResponseDTO;

public interface RelatorioPdfService {

    byte[] gerarRelatorioDashboard(DashboardResponseDTO dashboard);

    byte[] gerarRelatorioVendasSelecionadas(RelatorioVendasDTO solicitacao);

    byte[] gerarRelatorioClientes();
}
