package com.projeto.modelo.service.imp;

import com.projeto.modelo.controller.dto.response.dashboard.*;
import com.projeto.modelo.model.entity.Venda;
import com.projeto.modelo.repository.VendaRepository;
import com.projeto.modelo.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
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

    private BigDecimal calcularPercentualUpsell(LocalDate dataInicio, LocalDate dataFim) {
        long diasPeriodo = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
        // Vendas do período atual
        List<Venda> vendasPeriodoAtual = vendaRepository.findVendasFinalizadasNoPeriodo(
                dataInicio.atStartOfDay(), dataFim.atTime(23, 59, 59));
        // Vendas do período anterior
        List<Venda> vendasPeriodoAnterior = vendaRepository.findVendasFinalizadasNoPeriodo(
                dataInicio.minusDays(diasPeriodo).atStartOfDay(), dataInicio.minusDays(1).atTime(23, 59, 59));

        // Agrupar por cliente e pegar o maior valor de venda de cada período
        Map<Long, BigDecimal> maiorVendaAnterior = new HashMap<>();
        for (Venda v : vendasPeriodoAnterior) {
            Long idCliente = v.getCliente().getId();
            BigDecimal valor = v.getValorPago();
            maiorVendaAnterior.merge(idCliente, valor, BigDecimal::max);
        }
        Map<Long, BigDecimal> maiorVendaAtual = new HashMap<>();
        for (Venda v : vendasPeriodoAtual) {
            Long idCliente = v.getCliente().getId();
            BigDecimal valor = v.getValorPago();
            maiorVendaAtual.merge(idCliente, valor, BigDecimal::max);
        }

        // Calcular upsell: clientes cujo maior valor do período atual > maior valor do período anterior
        int totalClientesComparados = 0;
        int totalUpsell = 0;
        for (Map.Entry<Long, BigDecimal> entry : maiorVendaAtual.entrySet()) {
            Long idCliente = entry.getKey();
            BigDecimal valorAtual = entry.getValue();
            if (maiorVendaAnterior.containsKey(idCliente)) {
                totalClientesComparados++;
                if (valorAtual.compareTo(maiorVendaAnterior.get(idCliente)) > 0) {
                    totalUpsell++;
                }
            }
        }
        return totalClientesComparados > 0 ?
                BigDecimal.valueOf(totalUpsell).divide(BigDecimal.valueOf(totalClientesComparados), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

    private BigDecimal calcularPercentualChurn(LocalDate dataInicio, LocalDate dataFim) {
        long diasPeriodo = ChronoUnit.DAYS.between(dataInicio, dataFim) + 1;
        List<Venda> vendasPeriodo = vendaRepository.findVendasCanceladas(
                dataInicio.atStartOfDay(), dataFim.atTime(23, 59, 59));

        // Buscar vendas "churn" no período
        List<Venda> vendasChurn = new ArrayList<>();
        for (Venda v : vendasPeriodo) {
            if (v.getStatusPagamento() != null &&
                (v.getStatusPagamento().name().equals("REEMBOLSADO") || v.getStatusPagamento().name().equals("REEMBOLSO_SOLICITADO"))
                || (v.getStatusVenda() != null && v.getStatusVenda().name().equals("CANCELADO"))) {
                vendasChurn.add(v);
            }
        }
        // Total de clientes distintos que compraram no período
        Set<Long> clientesPeriodo = new HashSet<>();
        for (Venda v : vendasPeriodo) {
            clientesPeriodo.add(v.getCliente().getId());
        }
        // Total de clientes distintos que tiveram churn
        Set<Long> clientesChurn = new HashSet<>();
        for (Venda v : vendasChurn) {
            clientesChurn.add(v.getCliente().getId());
        }
        return clientesPeriodo.size() > 0 ?
                BigDecimal.valueOf(clientesChurn.size()).divide(BigDecimal.valueOf(clientesPeriodo.size()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
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

        List<Object[]> results = vendaRepository.getReembolsosUltimos30Dias();
        List<Reembolsos> reembolsos = results.stream()
                .map(row -> new Reembolsos(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        row[2] != null ? ((Timestamp) row[2]).toLocalDateTime() : null
                ))
                .toList();

        return DashboardResponseDTO.builder()
                .periodoCalculadoInicio(dataInicial)
                .periodoCalculadoFim(dataFim)
                .totalVendas(TotalVendas.builder()
                        .valorVendido(vendasPorPeriodo.totalValorPeriodoAtual().setScale(2, RoundingMode.HALF_UP))
                        .totalVendasAtual(vendasPorPeriodo.vendasTotaisPeriodoAtual())
                        .percentualPeriodoAnterior(calcularPercentualAnterior(vendasPorPeriodo.vendasTotaisPeriodoAtual(), vendasPorPeriodo.vendasTotaisPeriodoAnterior()))
                        .build())
                .ticketMedio(TicketMedio.builder()
                        .ticketMedio(vendasPorPeriodo.totalValorPeriodoAtual().doubleValue() > 0.0 ? vendasPorPeriodo.totalValorPeriodoAtual().divide(BigDecimal.valueOf(vendasPorPeriodo.vendasTotaisPeriodoAtual()), RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                        .percentualPeriodoAnterior(calcularPercentualAnterior(vendasPorPeriodo.vendasTotaisPeriodoAtual(), vendasPorPeriodo.vendasTotaisPeriodoAnterior()))
                        .build())
                .churn(Churn.builder()
                        .percentualChurnPeriodoAtual(calcularPercentualChurn(dataInicial, dataFim))
                        .percentualPeriodoAnterior(calcularPercentualChurn(dataInicial.minusDays(diasPeriodo - 1), dataInicial.minusDays(1)))
                        .build())
                .produtosMaisVendidos(vendaRepository.getProdutosMaisVendidos())
                .reembolsos30Dias(Reembolsos30Dias.builder()
                        .totalReembolsos(vendaRepository.getReembolsosUltimos30Dias().size())
                        .reembolsos(reembolsos)
                        .build())
                .upsell(Upsell.builder()
                        .percentualUpsellPeriodoAtual(calcularPercentualUpsell(dataInicial, dataFim))
                        .percentualPeriodoAnterior(calcularPercentualUpsell(dataInicial.minusDays(diasPeriodo - 1), dataInicial.minusDays(1)))
                        .build())
                .chargebackDTO(ChargebackDTO.builder()
                        .totalChargeback(vendaRepository.getTotalChargeback(dataInicial.atStartOfDay(), dataFim.atTime(23, 59, 59)))
                        .periodoCalculadoInicio(dataInicial)
                        .periodoCalculadoFim(dataFim)
                        .mesAmes(vendaRepository.getMesAMesChargeback(dataInicial.atStartOfDay(), dataFim.atTime(23, 59, 59)).stream().map(dto -> new MesAMesChargeback(
                                        dto.mesEnum(),
                                        dto.total()
                                ))
                                .toList())
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
                .mesAmes(vendaRepository.getMesAMesChargeback(dataInicial.atStartOfDay(), dataFim.atTime(23, 59, 59)).stream().map(dto -> new MesAMesChargeback(
                                dto.mesEnum(),
                                dto.total()
                        ))
                        .toList())
                .build();
    }
}
