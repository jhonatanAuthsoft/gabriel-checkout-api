package com.projeto.modelo.mapper;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.controller.dto.request.AtualizarPedidoAdmin;
import com.projeto.modelo.controller.dto.request.AtualizarVendaDTO;
import com.projeto.modelo.controller.dto.request.CriarVendaRequestDTO;
import com.projeto.modelo.controller.dto.response.VendaResponseDTO;
import com.projeto.modelo.model.entity.*;
import com.projeto.modelo.model.enums.*;
import com.projeto.modelo.repository.ProdutoRepository;
import com.projeto.modelo.repository.UsuarioRepository;
import com.projeto.modelo.repository.VendaRepository;
import com.projeto.modelo.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class VendaMapper {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private VendaRepository vendaRepository;

    public Venda toEntity(CriarVendaRequestDTO dto, Boolean primeiraVenda) {
        List<Produto> produtos = new ArrayList<>();
        List<Plano> planos = new ArrayList<>();
        Cupom cupom = null;
        Usuario vendedor = null;
        BigDecimal valorPago = BigDecimal.ZERO;

        if (dto.idsProduto() == null || dto.idsProduto().isEmpty()) {
            throw new ExcecoesCustomizada("A lista de produtos não pode estar vazia!", HttpStatus.BAD_REQUEST);
        }

        if (dto.idsPlano() == null || dto.idsPlano().isEmpty()) {
            throw new ExcecoesCustomizada("A lista de planos não pode estar vazia!", HttpStatus.BAD_REQUEST);
        }

        if (dto.idsProduto().size() != dto.idsPlano().size()) {
            throw new ExcecoesCustomizada("Cada produto deve ter um plano correspondente.", HttpStatus.BAD_REQUEST);
        }

        for (int i = 0; i < dto.idsProduto().size(); i++) {
            Long idProduto = dto.idsProduto().get(i);
            Long idPlano = dto.idsPlano().get(i);

            Produto produto = produtoRepository.findById(idProduto)
                    .orElseThrow(() -> new ExcecoesCustomizada("Produto com ID " + idProduto + " não encontrado", HttpStatus.NOT_FOUND));

            Long totalVendas = vendaRepository.contarVendasPorProduto(produto.getId());
            Long quantidadeMaxima = produto.getDadosProduto().disponibilidade().quantidadeMaxima();

            if (quantidadeMaxima != null && totalVendas >= quantidadeMaxima) {
                throw new ExcecoesCustomizada("Quantidade máxima atingida para o produto com ID " + idProduto, HttpStatus.BAD_REQUEST);
            }

            Plano plano = produto.getPlanos().stream()
                    .filter(p -> p.getId().equals(idPlano))
                    .findFirst()
                    .orElseThrow(() -> new ExcecoesCustomizada("Plano com ID " + idPlano + " não encontrado para o produto " + idProduto, HttpStatus.BAD_REQUEST));

            if (!plano.getStatus().equals(ProdutoStatus.ATIVO)) {
                throw new ExcecoesCustomizada("O plano com ID " + idPlano + " não está ativo", HttpStatus.BAD_REQUEST);
            }

            valorPago = valorPago.add(plano.getPreco());
            produtos.add(produto);
            planos.add(plano);
        }

        OrigemCompra origemCompra = primeiraVenda ? OrigemCompra.PRIMEIRA_COMPRA : OrigemCompra.RECORRENCIA;

        Usuario cliente = usuarioRepository.findById(dto.idCliente())
                .orElseThrow(() -> new ExcecoesCustomizada("Cliente não encontrado", HttpStatus.NOT_FOUND));

        if (dto.idVendedor() != null && dto.idVendedor() > 0) {
            vendedor = usuarioRepository.findById(dto.idVendedor())
                    .orElseThrow(() -> new ExcecoesCustomizada("Vendedor não encontrado", HttpStatus.NOT_FOUND));
        }

        // Aplicação de cupom (opcional)
        if (dto.codigoCupom() != null && !StringUtils.isNullOrEmpty(dto.codigoCupom())) {
            // Procura o cupom entre os produtos
            cupom = produtos.stream()
                    .flatMap(p -> p.getCupom().stream())
                    .filter(c -> c.getCodigoCupom().equals(dto.codigoCupom()))
                    .findFirst()
                    .orElseThrow(() -> new ExcecoesCustomizada("Cupom não encontrado ou inválido", HttpStatus.BAD_REQUEST));

            if (cupom.getStatus().equals(ProdutoStatus.INATIVO)) {
                throw new ExcecoesCustomizada("Cupom inativo", HttpStatus.BAD_REQUEST);
            }

            BigDecimal valorDesconto;
            if (cupom.getTipoDesconto().equals(TipoDesconto.VALOR)) {
                valorDesconto = cupom.getValor();
            } else {
                valorDesconto = valorPago.multiply(cupom.getValor()).divide(new BigDecimal(100), RoundingMode.HALF_EVEN);
            }

            valorPago = valorPago.subtract(valorDesconto);
            if (valorPago.compareTo(BigDecimal.ZERO) < 0) {
                valorPago = BigDecimal.ZERO;
            }
        }

        return Venda.builder()
                .produtos(produtos)
                .planos(planos)
                .valorPago(valorPago)
                .cupomUsado(cupom)
                .origemCompra(origemCompra)
                .statusVenda(StatusVenda.CARRINHO_ABANDONADO)
                .tipoRecorrencia(produtos.get(0).getDadosProduto().cobranca().tipoCobranca())
                .cliente(cliente)
                .vendedor(vendedor)
                .build();
    }

    public VendaResponseDTO toResponseDTO(Venda venda) {
        return VendaResponseDTO.builder()
                .id(venda.getId())
                .produtos(venda.getProdutos())
                .valorPago(venda.getValorPago())
                .txid(venda.getTxid())
                .codigoSolicitacao(venda.getCodigoSolicitacao())
                .cupomUsado(venda.getCupomUsado())
                .planos(venda.getPlanos())
                .origemCompra(venda.getOrigemCompra())
                .metodoPagamento(venda.getMetodoPagamento())
                .statusPagamento(venda.getStatusPagamento())
                .statusVenda(venda.getStatusVenda())
                .tipoRecorrencia(venda.getTipoRecorrencia())
                .cliente(venda.getCliente())
                .vendedor(venda.getVendedor())
                .dataCompra(venda.getDataCompra())
                .dataPagamento(venda.getDataPagamento())
                .dataAtualizacao(venda.getDataAtualizacao())
                .dataReembolso(venda.getDataReembolso())
                .build();
    }

    public void atualizarPedido(Venda venda, AtualizarPedidoAdmin dto) {
        Cupom cupom = null;
        Usuario vendedor = null;

        Usuario cliente = usuarioRepository.findById(dto.idCliente())
                .orElseThrow(() -> new ExcecoesCustomizada("Cliente não encontrado", HttpStatus.NOT_FOUND));

        if (dto.idVendedor() != null && dto.idVendedor() > 0) {
            vendedor = usuarioRepository.findById(dto.idVendedor())
                    .orElseThrow(() -> new ExcecoesCustomizada("Vendedor não encontrado", HttpStatus.NOT_FOUND));
        }

        // Buscar todos os produtos pelos IDs
        List<Produto> produtos = produtoRepository.findAllById(dto.idsProduto());
        if (produtos.size() != dto.idsProduto().size()) {
            throw new ExcecoesCustomizada("Um ou mais produtos não foram encontrados", HttpStatus.NOT_FOUND);
        }

        // Buscar planos dentro dos produtos
        List<Plano> planos = new ArrayList<>();
        for (Produto produto : produtos) {
            List<Plano> planosDoProduto = produto.getPlanos().stream()
                    .filter(p -> dto.idsPlano().contains(p.getId()))
                    .toList();

            planos.addAll(planosDoProduto);
        }

        // Validação: todos os planos devem ter sido encontrados
        if (planos.size() != dto.idsPlano().size()) {
            throw new ExcecoesCustomizada("Um ou mais planos não foram encontrados ou não pertencem aos produtos informados", HttpStatus.BAD_REQUEST);
        }

        // Cupom (opcional)
        if (dto.idCupomUsado() != null && dto.idCupomUsado() > 0) {
            Optional<Cupom> cupomOpt = produtos.stream()
                    .flatMap(p -> p.getCupom().stream())
                    .filter(c -> c.getId().equals(dto.idCupomUsado()))
                    .findFirst();

            if (cupomOpt.isEmpty()) {
                throw new ExcecoesCustomizada("Cupom não encontrado ou não pertence a esses produtos", HttpStatus.BAD_REQUEST);
            }

            cupom = cupomOpt.get();
        }

        // Atualização da venda
        venda.setValorPago(dto.valorPago());
        venda.setCupomUsado(cupom);
        venda.setOrigemCompra(dto.origemCompra());
        venda.setMetodoPagamento(dto.metodoPagamento());
        venda.setStatusPagamento(dto.statusPagamento());
        venda.setStatusVenda(dto.statusVenda());
        venda.setTipoRecorrencia(dto.tipoRecorrencia());
        venda.setCliente(cliente);
        venda.setVendedor(vendedor);
        venda.setDataReembolso(dto.dataReembolso());
        venda.setProdutos(produtos);
        venda.setPlanos(planos);
    }


    public void reembolsoConcluido(Venda venda) {
        if (venda.getStatusPagamento().equals(StatusPagamento.REEMBOLSO_SOLICITADO)) {
            venda.setStatusPagamento(StatusPagamento.REEMBOLSADO);
            venda.setDataReembolso(LocalDateTime.now());
        } else {
            throw new ExcecoesCustomizada("Para reembolsar um pedido ele tem que ter o status de Reembolso Solicitado!", HttpStatus.BAD_REQUEST);
        }
    }

    public void gerarPagamento(Venda venda, AtualizarVendaDTO dto) {
        if (!StringUtils.isNullOrEmpty(dto.txid())) {
            venda.setMetodoPagamento(MetodoPagamento.PIX);
            venda.setTxid(dto.txid());
            venda.setStatusPagamento(StatusPagamento.PENDENTE);
        } else if (!StringUtils.isNullOrEmpty(dto.codigoSolicitacao())) {
            venda.setMetodoPagamento(MetodoPagamento.BOLETO);
            venda.setCodigoSolicitacao(dto.codigoSolicitacao());
            venda.setStatusPagamento(StatusPagamento.PENDENTE);
        } else if (!StringUtils.isNullOrEmpty(dto.idPagamentoCartao())) {
            venda.setMetodoPagamento(MetodoPagamento.CARTAO);
            venda.setIdPagamentoCredito(dto.idPagamentoCartao());
            venda.setStatusPagamento(StatusPagamento.PENDENTE);
        }
    }

    public void confirmarPagamento(Venda venda, StatusPagamento statusPagamento, StatusVenda statusVenda, LocalDateTime dataPagamento) {
        venda.setStatusPagamento(statusPagamento);
        venda.setStatusVenda(statusVenda);
        venda.setDataPagamento(dataPagamento);
    }

    public Page<VendaResponseDTO> toResponseListDTO(Page<Venda> vendas) {
        return vendas.map(this::toResponseDTO);
    }
}
