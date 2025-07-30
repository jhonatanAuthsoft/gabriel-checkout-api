package com.projeto.modelo.service.imp;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.projeto.modelo.controller.dto.request.RelatorioVendasDTO;
import com.projeto.modelo.controller.dto.response.dashboard.*;
import com.projeto.modelo.model.entity.Venda;
import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.repository.VendaRepository;
import com.projeto.modelo.repository.UsuarioRepository;
import com.projeto.modelo.service.RelatorioPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelatorioPdfServiceImp implements RelatorioPdfService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public byte[] gerarRelatorioDashboard(DashboardResponseDTO dashboard) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Paragraph titulo = new Paragraph("Relatório de Vendas - Dashboard")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.BLUE);
            document.add(titulo);

            document.add(new Paragraph("Período: " + dashboard.periodoCalculadoInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a " + dashboard.periodoCalculadoFim().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15f));

            // Total de Vendas
            document.add(new Paragraph("Resumo Geral").setBold().setFontSize(14).setFontColor(ColorConstants.DARK_GRAY));
            Table resumo = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2})).useAllAvailableWidth();
            resumo.addHeaderCell(new Cell().add(new Paragraph("Total de Vendas")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            resumo.addHeaderCell(new Cell().add(new Paragraph("Valor Vendido")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            resumo.addHeaderCell(new Cell().add(new Paragraph("% Período Anterior")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            resumo.addCell(dashboard.totalVendas().totalVendasAtual().toString());
            resumo.addCell("R$ " + dashboard.totalVendas().valorVendido());
            resumo.addCell(dashboard.totalVendas().percentualPeriodoAnterior() + "%");
            document.add(resumo.setMarginBottom(10f));

            // Ticket Médio
            document.add(new Paragraph("Ticket Médio").setBold().setFontSize(14).setFontColor(ColorConstants.DARK_GRAY));
            Table ticket = new Table(UnitValue.createPercentArray(new float[]{2, 2})).useAllAvailableWidth();
            ticket.addHeaderCell(new Cell().add(new Paragraph("Ticket Médio")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            ticket.addHeaderCell(new Cell().add(new Paragraph("% Período Anterior")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            ticket.addCell("R$ " + dashboard.ticketMedio().ticketMedio());
            ticket.addCell(dashboard.ticketMedio().percentualPeriodoAnterior() + "%");
            document.add(ticket.setMarginBottom(10f));

            // Churn
            document.add(new Paragraph("Churn").setBold().setFontSize(14).setFontColor(ColorConstants.DARK_GRAY));
            Table churn = new Table(UnitValue.createPercentArray(new float[]{2, 2})).useAllAvailableWidth();
            churn.addHeaderCell(new Cell().add(new Paragraph("Churn Atual")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            churn.addHeaderCell(new Cell().add(new Paragraph("% Período Anterior")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            churn.addCell(dashboard.churn().percentualChurnPeriodoAtual() + "%");
            churn.addCell(dashboard.churn().percentualPeriodoAnterior() + "%");
            document.add(churn.setMarginBottom(10f));

            // Upsell
            document.add(new Paragraph("Upsell").setBold().setFontSize(14).setFontColor(ColorConstants.DARK_GRAY));
            Table upsell = new Table(UnitValue.createPercentArray(new float[]{2, 2})).useAllAvailableWidth();
            upsell.addHeaderCell(new Cell().add(new Paragraph("Upsell Atual")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            upsell.addHeaderCell(new Cell().add(new Paragraph("% Período Anterior")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            upsell.addCell(dashboard.upsell().percentualUpsellPeriodoAtual() + "%");
            upsell.addCell(dashboard.upsell().percentualPeriodoAnterior() + "%");
            document.add(upsell.setMarginBottom(10f));

            // Produtos mais vendidos
            document.add(new Paragraph("Produtos Mais Vendidos").setBold().setFontSize(14).setFontColor(ColorConstants.DARK_GRAY));
            Table produtos = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2, 2})).useAllAvailableWidth();
            produtos.addHeaderCell(new Cell().add(new Paragraph("#")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            produtos.addHeaderCell(new Cell().add(new Paragraph("Nome")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            produtos.addHeaderCell(new Cell().add(new Paragraph("Total Vendas")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            produtos.addHeaderCell(new Cell().add(new Paragraph("% das Vendas")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            int idx = 1;
            for (ProdutosMaisVendidos p : dashboard.produtosMaisVendidos()) {
                produtos.addCell(String.valueOf(idx++));
                produtos.addCell(p.nome());
                produtos.addCell(String.valueOf(p.totalVendas()));
                produtos.addCell(p.percentualDasVendas() + "%");
            }
            document.add(produtos.setMarginBottom(10f));

            // Reembolsos dos últimos 30 dias
            document.add(new Paragraph("Reembolsos nos Últimos 30 Dias").setBold().setFontSize(14).setFontColor(ColorConstants.DARK_GRAY));
            document.add(new Paragraph("Total de Reembolsos: " + dashboard.reembolsos30Dias().totalReembolsos()));
            Table reembolsos = new Table(UnitValue.createPercentArray(new float[]{2, 3, 3})).useAllAvailableWidth();
            reembolsos.addHeaderCell(new Cell().add(new Paragraph("ID Venda")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            reembolsos.addHeaderCell(new Cell().add(new Paragraph("Produto")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            reembolsos.addHeaderCell(new Cell().add(new Paragraph("Data Reembolso")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Reembolsos r : dashboard.reembolsos30Dias().reembolsos()) {
                reembolsos.addCell(String.valueOf(r.idVenda()));
                reembolsos.addCell(r.nome());
                reembolsos.addCell(r.dataReembolso() != null ? r.dataReembolso().format(dtf) : "");
            }
            document.add(reembolsos.setMarginBottom(10f));

            // Chargeback
            document.add(new Paragraph("Chargebacks").setBold().setFontSize(14).setFontColor(ColorConstants.DARK_GRAY));
            document.add(new Paragraph("Total de Chargebacks: " + dashboard.chargebackDTO().totalChargeback()));
            Table chargebacks = new Table(UnitValue.createPercentArray(new float[]{2, 2})).useAllAvailableWidth();
            chargebacks.addHeaderCell(new Cell().add(new Paragraph("Mês")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            chargebacks.addHeaderCell(new Cell().add(new Paragraph("Total")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            for (MesAMesChargeback c : dashboard.chargebackDTO().mesAmes()) {
                chargebacks.addCell(c.mes().name());
                chargebacks.addCell(String.valueOf(c.total()));
            }
            document.add(chargebacks.setMarginBottom(10f));

            // Vendas por período (gráfico tabular)
            document.add(new Paragraph("Vendas por Período (Mês a Mês)").setBold().setFontSize(14).setFontColor(ColorConstants.DARK_GRAY));
            Table vendasPeriodo = new Table(UnitValue.createPercentArray(new float[]{2, 2, 2, 2})).useAllAvailableWidth();
            vendasPeriodo.addHeaderCell(new Cell().add(new Paragraph("Mês")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            vendasPeriodo.addHeaderCell(new Cell().add(new Paragraph("Total Venda")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            vendasPeriodo.addHeaderCell(new Cell().add(new Paragraph("Total Campanha")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            vendasPeriodo.addHeaderCell(new Cell().add(new Paragraph("Total Link")).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold());
            for (MesAMesVendasPeriodoDTO v : dashboard.totalVendasPorPeriodoDTO().mesAmes()) {
                vendasPeriodo.addCell(v.mes().name());
                vendasPeriodo.addCell("R$ " + dashboard.totalVendas().valorVendido());
                vendasPeriodo.addCell(String.valueOf(v.totalCampanha()));
                vendasPeriodo.addCell(String.valueOf(v.totalLink()));
            }
            document.add(vendasPeriodo.setMarginBottom(10f));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
    }

    public byte[] gerarRelatorioVendasSelecionadas(RelatorioVendasDTO solicitacao) {
        List<Venda> vendas = vendaRepository.findAllById(solicitacao.ids());
        vendas.sort(Comparator.comparing(Venda::getId));

        // Totais
        BigDecimal valorTotal = vendas.stream()
                .filter(v -> v.getValorPago() != null)
                .map(Venda::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFinalizada = vendas.stream()
                .filter(v -> v.getValorPago() != null && v.getStatusVenda() != null && v.getStatusVenda().name().equals("FINALIZADO"))
                .map(Venda::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReembolsado = vendas.stream()
                .filter(v -> v.getValorPago() != null && v.getStatusPagamento() != null && v.getStatusPagamento().name().equals("REEMBOLSADO"))
                .map(Venda::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal resultado = totalFinalizada.subtract(totalReembolsado);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Paragraph titulo = new Paragraph("Relatório de Vendas Selecionadas")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.BLUE);
            document.add(titulo);

            // Tabela de totais
            Table totais = new Table(UnitValue.createPercentArray(new float[]{2, 2})).useAllAvailableWidth();
            totais.addHeaderCell(new Cell().add(new Paragraph("Total de Venda Finalizada").setFontSize(10))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold().setTextAlignment(TextAlignment.CENTER));
            totais.addHeaderCell(new Cell().add(new Paragraph("Total Reembolsado").setFontSize(10))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold().setTextAlignment(TextAlignment.CENTER));
            totais.addCell(new Cell().add(new Paragraph("R$ " + totalFinalizada).setFontSize(10))
                    .setTextAlignment(TextAlignment.CENTER));
            totais.addCell(new Cell().add(new Paragraph("R$ " + totalReembolsado).setFontSize(10))
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(totais.setMarginBottom(10f));

            // Tabela de vendas
            float fontSize = 9f;
            Table tabela = new Table(UnitValue.createPercentArray(new float[]{1, 3, 3, 2, 2, 2, 2, 2, 2}))
                    .useAllAvailableWidth();

            // Cabeçalhos
            String[] headers = {
                    "ID Venda", "Cliente", "Produtos", "Planos", "Valor Pago",
                    "Método Pagamento", "Data Compra", "Status Pagamento", "Status Venda"
            };

            for (String header : headers) {
                tabela.addHeaderCell(new Cell()
                        .add(new Paragraph(header).setFontSize(fontSize))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setBold()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            for (Venda v : vendas) {
                tabela.addCell(new Cell().add(new Paragraph(String.valueOf(v.getId())).setFontSize(fontSize))
                        .setTextAlignment(TextAlignment.CENTER));

                tabela.addCell(new Cell().add(new Paragraph(v.getCliente() != null ? v.getCliente().getNome() : "")
                        .setFontSize(fontSize)).setTextAlignment(TextAlignment.CENTER));

                // Produtos (lista)
                String nomeProdutos = v.getProdutos() != null
                        ? v.getProdutos().stream()
                        .map(p -> p.getDadosProduto() != null && p.getDadosProduto().dadosGerais() != null
                                ? p.getDadosProduto().dadosGerais().nome()
                                : "(Produto sem nome)")
                        .collect(Collectors.joining(", "))
                        : "";
                tabela.addCell(new Cell().add(new Paragraph(nomeProdutos).setFontSize(fontSize))
                        .setTextAlignment(TextAlignment.CENTER));

                // Planos (lista)
                String nomePlanos = v.getPlanos() != null
                        ? v.getPlanos().stream()
                        .map(p -> p.getNome() != null ? p.getNome() : "(Plano sem nome)")
                        .collect(Collectors.joining(", "))
                        : "";
                tabela.addCell(new Cell().add(new Paragraph(nomePlanos).setFontSize(fontSize))
                        .setTextAlignment(TextAlignment.CENTER));

                tabela.addCell(new Cell().add(new Paragraph(v.getValorPago() != null ? "R$ " + v.getValorPago() : "")
                        .setFontSize(fontSize)).setTextAlignment(TextAlignment.CENTER));

                tabela.addCell(new Cell().add(new Paragraph(v.getMetodoPagamento() != null
                                ? v.getMetodoPagamento().name() : "").setFontSize(fontSize))
                        .setTextAlignment(TextAlignment.CENTER));

                tabela.addCell(new Cell().add(new Paragraph(v.getDataCompra() != null
                                ? v.getDataCompra().format(dtf) : "").setFontSize(fontSize))
                        .setTextAlignment(TextAlignment.CENTER));

                tabela.addCell(new Cell().add(new Paragraph(v.getStatusPagamento() != null
                                ? v.getStatusPagamento().name() : "").setFontSize(fontSize))
                        .setTextAlignment(TextAlignment.CENTER));

                tabela.addCell(new Cell().add(new Paragraph(v.getStatusVenda() != null
                                ? v.getStatusVenda().name() : "").setFontSize(fontSize))
                        .setTextAlignment(TextAlignment.CENTER));
            }

            document.add(tabela);
            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF de vendas selecionadas", e);
        }
    }

    public byte[] gerarRelatorioClientes(LocalDate dataInicial, LocalDate dataFim) {
        List<Usuario> clientes = usuarioRepository.findAll().stream()
                .filter(u -> u.getPermissao() != null && u.getPermissao().name().equals("CLIENTE"))
                .filter(u -> {
                    LocalDate dataCriacao = u.getDataCriacao().toLocalDate();
                    return (dataCriacao.isEqual(dataInicial) || dataCriacao.isAfter(dataInicial)) &&
                           (dataCriacao.isEqual(dataFim) || dataCriacao.isBefore(dataFim));
                })
                .toList();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Paragraph titulo = new Paragraph("Relatório de Clientes")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.BLUE);
            document.add(titulo);

            float fontSize = 9f;
            Table tabela = new Table(UnitValue.createPercentArray(new float[]{3, 4, 3, 3, 3, 3})).useAllAvailableWidth();
            tabela.addHeaderCell(new Cell().add(new Paragraph("Nome").setFontSize(fontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold().setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
            tabela.addHeaderCell(new Cell().add(new Paragraph("Email").setFontSize(fontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold().setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
            tabela.addHeaderCell(new Cell().add(new Paragraph("Telefone").setFontSize(fontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold().setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
            tabela.addHeaderCell(new Cell().add(new Paragraph("Estado").setFontSize(fontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold().setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
            tabela.addHeaderCell(new Cell().add(new Paragraph("Total Comprado").setFontSize(fontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold().setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
            tabela.addHeaderCell(new Cell().add(new Paragraph("Total Reembolsado").setFontSize(fontSize)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setBold().setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
            for (Usuario u : clientes) {
                BigDecimal totalComprado = vendaRepository.findAll().stream()
                        .filter(v -> v.getCliente() != null && v.getCliente().getId().equals(u.getId()) && v.getStatusVenda() != null && v.getValorPago() != null)
                        .map(com.projeto.modelo.model.entity.Venda::getValorPago)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal totalReembolsado = vendaRepository.findAll().stream()
                        .filter(v -> v.getCliente() != null && v.getCliente().getId().equals(u.getId()) && v.getStatusPagamento() != null && v.getStatusPagamento().name().equals("REEMBOLSADO") && v.getValorPago() != null)
                        .map(com.projeto.modelo.model.entity.Venda::getValorPago)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                tabela.addCell(new Cell().add(new Paragraph(u.getNome() != null ? u.getNome() : "").setFontSize(fontSize)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
                tabela.addCell(new Cell().add(new Paragraph(u.getEmail() != null ? u.getEmail() : "").setFontSize(fontSize)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
                tabela.addCell(new Cell().add(new Paragraph(u.getCelular() != null ? u.getCelular() : "").setFontSize(fontSize)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
                tabela.addCell(new Cell().add(new Paragraph(u.getEndereco().uf() != null ? u.getEndereco().uf().toString() : "").setFontSize(fontSize)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
                tabela.addCell(new Cell().add(new Paragraph("R$ " + totalComprado).setFontSize(fontSize)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
                tabela.addCell(new Cell().add(new Paragraph("R$ " + totalReembolsado).setFontSize(fontSize)).setTextAlignment(TextAlignment.CENTER).setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE));
            }
            document.add(tabela);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF de clientes", e);
        }
    }
} 