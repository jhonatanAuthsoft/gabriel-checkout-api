package com.projeto.modelo.controller;

import com.projeto.modelo.controller.dto.request.RelatorioVendasDTO;
import com.projeto.modelo.controller.dto.response.dashboard.DashboardResponseDTO;
import com.projeto.modelo.service.DashboardService;
import com.projeto.modelo.service.imp.RelatorioExcelServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/relatorio/excel")
public class RelatorioExcelController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private RelatorioExcelServiceImp relatorioExcelServiceImp;

    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @GetMapping(value = "/dashboard", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> gerarRelatorioDashboardExcel(
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        DashboardResponseDTO dashboard = dashboardService.carregarDashboard(dataInicial, dataFim);
        byte[] excel = relatorioExcelServiceImp.gerarRelatorioDashboardExcel(dashboard);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-dashboard.xlsx");
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        return new ResponseEntity<>(excel, headers, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @PostMapping(value = "/vendas", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> gerarRelatorioVendasSelecionadasExcel(@RequestBody RelatorioVendasDTO ids) {
        byte[] excel = relatorioExcelServiceImp.gerarRelatorioVendasSelecionadasExcel(ids);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-vendas-selecionadas.xlsx");
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        return new ResponseEntity<>(excel, headers, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @GetMapping(value = "/clientes", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> gerarRelatoriosDeClientesExcel(@RequestParam(value = "dataInicial", defaultValue = "1999-01-01", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
                                                                 @RequestParam(value = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        if (dataFim == null){
            dataFim = LocalDate.now();
        }
        byte[] excel = relatorioExcelServiceImp.gerarRelatorioClientesExcel(dataInicial, dataFim);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-clientes.xlsx");
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        return new ResponseEntity<>(excel, headers, HttpStatus.OK);
    }
}
