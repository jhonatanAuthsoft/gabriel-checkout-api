package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.RelatorioVendasDTO;
import com.projeto.modelo.controller.dto.response.dashboard.DashboardResponseDTO;

public interface RelatorioExcelService {

    byte[] gerarRelatorioDashboardExcel(DashboardResponseDTO dashboard);

    byte[] gerarRelatorioVendasSelecionadasExcel(RelatorioVendasDTO solicitacao);

    byte[] gerarRelatorioClientesExcel();

}
