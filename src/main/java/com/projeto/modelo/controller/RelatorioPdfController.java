package com.projeto.modelo.controller;

import com.projeto.modelo.controller.dto.request.RelatorioVendasDTO;
import com.projeto.modelo.controller.dto.response.dashboard.DashboardResponseDTO;
import com.projeto.modelo.service.DashboardService;
import com.projeto.modelo.service.imp.RelatorioPdfServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;

@RestController
@RequestMapping("/relatorio/pdf")
public class RelatorioPdfController {

    @Autowired
    private DashboardService dashboardService;
    @Autowired
    private RelatorioPdfServiceImp relatorioPdfServiceImp;

    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @GetMapping(value = "/dashboard", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> gerarRelatorioDashboard(
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        DashboardResponseDTO dashboard = dashboardService.carregarDashboard(dataInicial, dataFim);
        byte[] pdf = relatorioPdfServiceImp.gerarRelatorioDashboard(dashboard);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-dashboard.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);

    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @PostMapping(value = "/vendas", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> gerarRelatorioVendasSelecionadas(@RequestBody RelatorioVendasDTO ids) {
        byte[] pdf = relatorioPdfServiceImp.gerarRelatorioVendasSelecionadas(ids);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-vendas-selecionadas.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @GetMapping(value = "/clientes", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> gerarRelatoriosDeClientes(@RequestParam(value = "dataInicial", defaultValue = "1999-01-01", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
                                                            @RequestParam(value = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        if (dataFim == null){
            dataFim = LocalDate.now();
        }
        byte[] pdf = relatorioPdfServiceImp.gerarRelatorioClientes(dataInicial, dataFim);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-clientes.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
} 