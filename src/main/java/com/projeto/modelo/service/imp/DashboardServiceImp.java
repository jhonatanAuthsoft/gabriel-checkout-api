package com.projeto.modelo.service.imp;

import com.projeto.modelo.controller.dto.response.dashboard.*;
import com.projeto.modelo.repository.VendaRepository;
import com.projeto.modelo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class DashboardServiceImp implements DashboardService {

    @Autowired
    private VendaRepository vendaRepository;

    private BigDecimal calcularPercentualAnterior(Long vendasTotaisPeriodoAtual, Long vendasTotaisPeriodoAnterior) {
        if (vendasTotaisPeriodoAtual == null || vendasTotaisPeriodoAtual == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalAtual = BigDecimal.valueOf(vendasTotaisPeriodoAtual);
        BigDecimal totalAnterior = BigDecimal.valueOf(vendasTotaisPeriodoAnterior != null ? vendasTotaisPeriodoAnterior : 0);

        return totalAnterior
                .divide(totalAtual, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public DashboardResponseDTO carregarDashboard(LocalDate dataInicial, LocalDate dataFim) {
        long diasPeriodo = ChronoUnit.DAYS.between(dataInicial, dataFim) + 1;

        VendasPeriodoResponseDB vendasPorPeriodo = vendaRepository.getVendasPorPeriodo(
                dataInicial.atStartOfDay(),
                dataFim.atTime(23, 59, 59),
                dataInicial.minusDays(diasPeriodo - 1).atStartOfDay(),
                dataFim.minusDays(1).atTime(23, 59, 59)
        );

        // Exemplo de cálculo de churn e upsell (ajuste conforme regra de negócio)
        BigDecimal churnAtual = BigDecimal.ZERO; // Calcule conforme sua regra
        BigDecimal churnAnterior = BigDecimal.ZERO; // Calcule conforme sua regra
        BigDecimal upsellAtual = BigDecimal.ZERO; // Calcule conforme sua regra
        BigDecimal upsellAnterior = BigDecimal.ZERO; // Calcule conforme sua regra

        return DashboardResponseDTO.builder()
                .periodoCalculadoInicio(dataInicial)
                .periodoCalculadoFim(dataFim)
                .totalVendas(TotalVendas.builder()
                        .valorVendido(vendasPorPeriodo.totalValorPeriodoAtual().setScale(2, RoundingMode.HALF_UP))
                        .totalVendasAtual(vendasPorPeriodo.vendasTotaisPeriodoAtual())
                        .percentualPeriodoAnterior(calcularPercentualAnterior(vendasPorPeriodo.vendasTotaisPeriodoAtual(), vendasPorPeriodo.vendasTotaisPeriodoAnterior()))
                        .build())
                .ticketMedio(TicketMedio.builder()
                        .ticketMedio(vendasPorPeriodo.totalValorPeriodoAtual().divide(BigDecimal.valueOf(vendasPorPeriodo.vendasTotaisPeriodoAtual()), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP))
                        .percentualPeriodoAnterior(calcularPercentualAnterior(vendasPorPeriodo.vendasTotaisPeriodoAtual(), vendasPorPeriodo.vendasTotaisPeriodoAnterior()))
                        .build())
                .churn(Churn.builder()
                        .percentualChurnPeriodoAtual(churnAtual)
                        .percentualPeriodoAnterior(churnAnterior)
                        .build())
                .produtosMaisVendidos(vendaRepository.getProdutosMaisVendidos())
                .reembolsos30Dias(Reembolsos30Dias.builder()
                        .totalReembolsos(vendaRepository.getReembolsosUltimos30Dias().size()) //
                        .reembolsos(vendaRepository.getReembolsosUltimos30Dias())
                        .build())
                .upsell(Upsell.builder()
                        .percentualUpsellPeriodoAtual(upsellAtual)
                        .percentualPeriodoAnterior(upsellAnterior)
                        .build())
                .chargebackDTO(ChargebackDTO.builder()
                        .totalChargeback(vendaRepository.getTotalChargeback(dataInicial.atStartOfDay(), dataFim.atTime(23, 59, 59)))
                        .periodoCalculadoInicio(dataInicial)
                        .periodoCalculadoFim(dataFim)
                        .mesAmes(vendaRepository.getMesAMesChargeback(dataInicial.atStartOfDay(), dataFim.atTime(23, 59, 59)))
                        .build())
                .totalVendasPorPeriodoDTO(TotalVendasPorPeriodoDTO.builder()
                        .periodoCalculadoInicio(dataInicial)
                        .periodoCalculadoFim(dataFim)
                        .mesAmes(vendaRepository.getMesAMesVendasPorPeriodo(dataInicial.atStartOfDay(), dataFim.atTime(23, 59, 59)).stream().map(dto -> new MesAMesVendasPeriodoDTO(
                                        dto.mesEnum(),
                                        dto.totalVenda(),
                                        dto.totalCampanha(),
                                        dto.totalLink()
                                ))
                                .toList())
                        .build())
                .build();
    }

    @Override
    public TotalVendasPorPeriodoDTO carregarVendaPeriodo(LocalDate dataInicial, LocalDate dataFim) {
        return TotalVendasPorPeriodoDTO.builder()
                .periodoCalculadoInicio(dataInicial)
                .periodoCalculadoFim(dataFim)
                .mesAmes(vendaRepository.getMesAMesVendasPorPeriodo(dataInicial.atStartOfDay(), dataFim.atTime(23, 59, 59)).stream().map(dto -> new MesAMesVendasPeriodoDTO(
                                dto.mesEnum(),
                                dto.totalVenda(),
                                dto.totalCampanha(),
                                dto.totalLink()
                        ))
                        .toList())
                .build();
    }

    @Override
    public ChargebackDTO carregarChargeback(LocalDate dataInicial, LocalDate dataFim) {
        return ChargebackDTO.builder()
                .totalChargeback(vendaRepository.getTotalChargeback(dataInicial.atStartOfDay(), dataFim.atTime(23, 59, 59)))
                .periodoCalculadoInicio(dataInicial)
                .periodoCalculadoFim(dataFim)
                .mesAmes(vendaRepository.getMesAMesChargeback(dataInicial.atStartOfDay(), dataFim.atTime(23, 59, 59)))
                .build();
    }
}
