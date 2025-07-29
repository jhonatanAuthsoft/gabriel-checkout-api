package com.projeto.modelo.controller;

import com.projeto.modelo.controller.dto.response.dashboard.ChargebackDTO;
import com.projeto.modelo.controller.dto.response.dashboard.DashboardResponseDTO;
import com.projeto.modelo.controller.dto.response.dashboard.TotalVendasPorPeriodoDTO;
import com.projeto.modelo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> carregarDashboard(
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        return new ResponseEntity<>(dashboardService.carregarDashboard(dataInicial, dataFim), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @GetMapping("/vendas-periodo")
    public ResponseEntity<TotalVendasPorPeriodoDTO> carregarVendaPeriodo(
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        return new ResponseEntity<>(dashboardService.carregarVendaPeriodo(dataInicial, dataFim), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @GetMapping("/chargeback")
    public ResponseEntity<ChargebackDTO> carregarChargeback(
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim
    ) {
        return new ResponseEntity<>(dashboardService.carregarChargeback(dataInicial, dataFim), HttpStatus.OK);
    }
} 