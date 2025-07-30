package com.projeto.modelo.service.imp;

import com.projeto.modelo.controller.dto.request.RelatorioVendasDTO;
import com.projeto.modelo.controller.dto.response.dashboard.*;
import com.projeto.modelo.model.entity.Venda;
import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.model.entity.Plano;
import com.projeto.modelo.model.entity.Produto;
import com.projeto.modelo.model.entity.Cupom;
import com.projeto.modelo.model.entity.produto.DadosProduto;
import com.projeto.modelo.model.entity.produto.DadosGerais;
import com.projeto.modelo.repository.VendaRepository;
import com.projeto.modelo.repository.UsuarioRepository;
import com.projeto.modelo.service.RelatorioExcelService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelatorioExcelServiceImp implements RelatorioExcelService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public byte[] gerarRelatorioDashboardExcel(DashboardResponseDTO dashboard) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Dashboard");
            int rowIdx = 0;
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Relatório de Vendas - Dashboard");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Período: " + dashboard.periodoCalculadoInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a " + dashboard.periodoCalculadoFim().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            // Resumo Geral
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Resumo Geral");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Total de Vendas");
            row.createCell(1).setCellValue("Valor Vendido");
            row.createCell(2).setCellValue("% Período Anterior");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(dashboard.totalVendas().totalVendasAtual());
            row.createCell(1).setCellValue("R$ " + dashboard.totalVendas().valorVendido());
            row.createCell(2).setCellValue(dashboard.totalVendas().percentualPeriodoAnterior() + "%");

            // Ticket Médio
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Ticket Médio");
            row.createCell(1).setCellValue("% Período Anterior");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("R$ " + dashboard.ticketMedio().ticketMedio());
            row.createCell(1).setCellValue(dashboard.ticketMedio().percentualPeriodoAnterior() + "%");

            // Churn
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Churn Atual");
            row.createCell(1).setCellValue("% Período Anterior");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(dashboard.churn().percentualChurnPeriodoAtual() + "%");
            row.createCell(1).setCellValue(dashboard.churn().percentualPeriodoAnterior() + "%");

            // Upsell
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Upsell Atual");
            row.createCell(1).setCellValue("% Período Anterior");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(dashboard.upsell().percentualUpsellPeriodoAtual() + "%");
            row.createCell(1).setCellValue(dashboard.upsell().percentualPeriodoAnterior() + "%");

            // Produtos mais vendidos
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Produtos Mais Vendidos");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("#");
            row.createCell(1).setCellValue("Nome");
            row.createCell(2).setCellValue("Total Vendas");
            row.createCell(3).setCellValue("% das Vendas");
            int idx = 1;
            for (ProdutosMaisVendidos p : dashboard.produtosMaisVendidos()) {
                row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(idx++);
                row.createCell(1).setCellValue(p.nome());
                row.createCell(2).setCellValue(p.totalVendas());
                row.createCell(3).setCellValue(p.percentualDasVendas() + "%");
            }

            // Reembolsos dos últimos 30 dias
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Reembolsos nos Últimos 30 Dias");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Total de Reembolsos: " + dashboard.reembolsos30Dias().totalReembolsos());
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("ID Venda");
            row.createCell(1).setCellValue("Produto");
            row.createCell(2).setCellValue("Data Reembolso");
            for (Reembolsos r : dashboard.reembolsos30Dias().reembolsos()) {
                row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(r.idVenda());
                row.createCell(1).setCellValue(r.nome());
                row.createCell(2).setCellValue(r.dataReembolso() != null ? r.dataReembolso().format(dtf) : "");
            }

            // Chargebacks
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Chargebacks");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Total de Chargebacks: " + dashboard.chargebackDTO().totalChargeback());
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Mês");
            row.createCell(1).setCellValue("Total");
            for (MesAMesChargeback c : dashboard.chargebackDTO().mesAmes()) {
                row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(c.mes().name());
                row.createCell(1).setCellValue(c.total());
            }

            // Vendas por período
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Vendas por Período (Mês a Mês)");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Mês");
            row.createCell(1).setCellValue("Total Venda");
            row.createCell(2).setCellValue("Total Campanha");
            row.createCell(3).setCellValue("Total Link");
            for (MesAMesVendasPeriodoDTO v : dashboard.totalVendasPorPeriodoDTO().mesAmes()) {
                row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(v.mes().name());
                row.createCell(1).setCellValue(v.totalVenda().doubleValue());
                row.createCell(2).setCellValue(v.totalCampanha().doubleValue());
                row.createCell(3).setCellValue(v.totalLink().doubleValue());
            }

            for (int i = 0; i < 10; i++) sheet.autoSizeColumn(i);
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar Excel do dashboard", e);
        }
    }

    @Override
    public byte[] gerarRelatorioVendasSelecionadasExcel(RelatorioVendasDTO solicitacao) {
        List<Venda> vendas = vendaRepository.findAllById(solicitacao.ids());
        vendas.sort(java.util.Comparator.comparing(Venda::getId));

        BigDecimal totalFinalizada = vendas.stream()
                .filter(v -> v.getValorPago() != null
                             && v.getStatusVenda() != null
                             && v.getStatusVenda().name().equals("FINALIZADO"))
                .map(Venda::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReembolsado = vendas.stream()
                .filter(v -> v.getValorPago() != null
                             && v.getStatusPagamento() != null
                             && v.getStatusPagamento().name().equals("REEMBOLSADO"))
                .map(Venda::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Vendas Selecionadas");
            int rowIdx = 0;

            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Relatório de Vendas Selecionadas");

            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Total de Venda Finalizada");
            row.createCell(1).setCellValue("Total Reembolsado");

            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("R$ " + totalFinalizada);
            row.createCell(1).setCellValue("R$ " + totalReembolsado);

            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(""); // Espaço

            // Cabeçalhos
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("ID Venda");
            row.createCell(1).setCellValue("Cliente");
            row.createCell(2).setCellValue("Produtos");
            row.createCell(3).setCellValue("Planos");
            row.createCell(4).setCellValue("Valor Pago");
            row.createCell(5).setCellValue("Método Pagamento");
            row.createCell(6).setCellValue("Data Compra");
            row.createCell(7).setCellValue("Status Pagamento");
            row.createCell(8).setCellValue("Status Venda");

            for (Venda v : vendas) {
                row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(v.getId());
                row.createCell(1).setCellValue(v.getCliente() != null ? v.getCliente().getNome() : "");

                // Produtos
                String nomeProdutos = v.getProdutos() != null
                        ? v.getProdutos().stream()
                        .map(p -> {
                            if (p.getDadosProduto() != null && p.getDadosProduto().dadosGerais() != null) {
                                return p.getDadosProduto().dadosGerais().nome();
                            }
                            return "(Produto sem nome)";
                        })
                        .collect(Collectors.joining(", "))
                        : "";
                row.createCell(2).setCellValue(nomeProdutos);

                // Planos
                String nomePlanos = v.getPlanos() != null
                        ? v.getPlanos().stream()
                        .map(p -> p.getNome() != null ? p.getNome() : "(Plano sem nome)")
                        .collect(Collectors.joining(", "))
                        : "";
                row.createCell(3).setCellValue(nomePlanos);

                row.createCell(4).setCellValue(v.getValorPago() != null ? "R$ " + v.getValorPago() : "");
                row.createCell(5).setCellValue(v.getMetodoPagamento() != null ? v.getMetodoPagamento().name() : "");
                row.createCell(6).setCellValue(v.getDataCompra() != null ? v.getDataCompra().format(dtf) : "");
                row.createCell(7).setCellValue(v.getStatusPagamento() != null ? v.getStatusPagamento().name() : "");
                row.createCell(8).setCellValue(v.getStatusVenda() != null ? v.getStatusVenda().name() : "");
            }

            for (int i = 0; i < 10; i++) sheet.autoSizeColumn(i);

            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar Excel de vendas selecionadas", e);
        }
    }

    @Override
    public byte[] gerarRelatorioClientesExcel(LocalDate dataInicial, LocalDate dataFim) {
        List<Usuario> clientes = usuarioRepository.findAll().stream()
                .filter(u -> u.getPermissao() != null && u.getPermissao().name().equals("CLIENTE"))
                .filter(u -> {
                    LocalDate dataCriacao = u.getDataCriacao().toLocalDate();
                    return (dataCriacao.isEqual(dataInicial) || dataCriacao.isAfter(dataInicial)) &&
                           (dataCriacao.isEqual(dataFim) || dataCriacao.isBefore(dataFim));
                })
                .toList();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Clientes");
            int rowIdx = 0;
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Relatório de Clientes");
            row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue("Nome");
            row.createCell(1).setCellValue("Email");
            row.createCell(2).setCellValue("Telefone");
            row.createCell(3).setCellValue("Estado");
            row.createCell(4).setCellValue("Total Comprado");
            row.createCell(5).setCellValue("Total Reembolsado");
            for (Usuario u : clientes) {
                BigDecimal totalComprado = vendaRepository.findAll().stream()
                        .filter(v -> v.getCliente() != null && v.getCliente().getId().equals(u.getId()) && v.getStatusVenda() != null && v.getValorPago() != null)
                        .map(Venda::getValorPago)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalReembolsado = vendaRepository.findAll().stream()
                        .filter(v -> v.getCliente() != null && v.getCliente().getId().equals(u.getId()) && v.getStatusPagamento() != null && v.getStatusPagamento().name().equals("REEMBOLSADO") && v.getValorPago() != null)
                        .map(Venda::getValorPago)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(u.getNome() != null ? u.getNome() : "");
                row.createCell(1).setCellValue(u.getEmail() != null ? u.getEmail() : "");
                row.createCell(2).setCellValue(u.getCelular() != null ? u.getCelular() : "");
                row.createCell(3).setCellValue(u.getEndereco() != null && u.getEndereco().uf() != null ? u.getEndereco().uf().toString() : "");
                row.createCell(4).setCellValue("R$ " + totalComprado);
                row.createCell(5).setCellValue("R$ " + totalReembolsado);
            }
            for (int i = 0; i < 6; i++) sheet.autoSizeColumn(i);
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar Excel de clientes", e);
        }
    }
}