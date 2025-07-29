package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.RelatorioVendasDTO;
import com.projeto.modelo.controller.dto.response.dashboard.DashboardResponseDTO;

import java.time.LocalDate;

public interface RelatorioExcelService {

    byte[] gerarRelatorioDashboardExcel(DashboardResponseDTO dashboard);

    byte[] gerarRelatorioVendasSelecionadasExcel(RelatorioVendasDTO solicitacao);

    byte[] gerarRelatorioClientesExcel(LocalDate dataInicial, LocalDate dataFim);

}
