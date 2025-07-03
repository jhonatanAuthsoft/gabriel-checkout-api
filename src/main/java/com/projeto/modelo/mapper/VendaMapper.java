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
import com.projeto.modelo.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class VendaMapper {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public Venda toEntity(CriarVendaRequestDTO dto, Boolean primeiraVenda) {
        Cupom cupom = null;
        Usuario vendedor = null;

        Produto produto = produtoRepository.findById(dto.idProduto()).orElseThrow(() -> new ExcecoesCustomizada("Produto não encontrado", HttpStatus.NOT_FOUND));
        Plano plano = produto.getPlanos().stream()
                .filter(p -> p.getId().equals(dto.idPlano()))
                .findFirst()
                .orElseThrow(() -> new ExcecoesCustomizada("Plano não encontrado!", HttpStatus.BAD_REQUEST));

        BigDecimal valorPago = plano.getPreco();

        OrigemCompra origemCompra = primeiraVenda ? OrigemCompra.PRIMEIRA_COMPRA : OrigemCompra.RECORRENCIA;

        Usuario cliente = usuarioRepository.findById(dto.idCliente()).orElseThrow(() -> new ExcecoesCustomizada("Cliente não encontrado", HttpStatus.NOT_FOUND));
        if (dto.idVendedor() != null && dto.idVendedor() > 0) {
            vendedor = usuarioRepository.findById(dto.idCliente()).orElseThrow(() -> new ExcecoesCustomizada("Cliente não encontrado", HttpStatus.NOT_FOUND));
        }

        if (dto.codigoCupom() != null && StringUtils.isNullOrEmpty(dto.codigoCupom())) {
            cupom = produto.getCupom().stream()
                    .filter(c -> c.getCodigoCupom().equals(dto.codigoCupom()))
                    .findFirst()
                    .orElseThrow(() -> new ExcecoesCustomizada("Cupom não encontrado ou inválido", HttpStatus.BAD_REQUEST));

            if (cupom.getStatus().equals(ProdutoStatus.INATIVO))
                throw new ExcecoesCustomizada("Cupom não encontrado ou inválido", HttpStatus.BAD_REQUEST);

            BigDecimal valorDesconto;

            if (cupom.getTipoDesconto().equals(TipoDesconto.VALOR)) {
                valorDesconto = cupom.getValor();
            } else {
                valorDesconto = valorPago.multiply(cupom.getValor());
            }

            valorPago = valorPago.subtract(valorDesconto);
        }

        return Venda.builder()
                .produto(produto)
                .valorPago(valorPago)
                .plano(plano)
                .cupomUsado(cupom)
                .origemCompra(origemCompra)
                .statusVenda(StatusVenda.CARRINHO_ABANDONADO)
                .tipoRecorrencia(produto.getDadosProduto().cobranca().tipoCobranca())
                .cliente(cliente)
                .vendedor(vendedor)
                .build();
    }

    public VendaResponseDTO toResponseDTO(Venda venda) {
        return VendaResponseDTO.builder()
                .id(venda.getId())
                .produto(venda.getProduto())
                .valorPago(venda.getValorPago())
                .txid(venda.getTxid())
                .codigoSolicitacao(venda.getCodigoSolicitacao())
                .cupomUsado(venda.getCupomUsado())
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
                .build();
    }

    public void atualizarPedido(Venda venda, AtualizarPedidoAdmin dto) {
        Cupom cupom = null;
        Usuario vendedor = null;

        Usuario cliente = usuarioRepository.findById(dto.idCliente()).orElseThrow(() -> new ExcecoesCustomizada("Cliente não encontrado", HttpStatus.NOT_FOUND));
        if (dto.idVendedor() != null && dto.idVendedor() > 0) {
            vendedor = usuarioRepository.findById(dto.idCliente()).orElseThrow(() -> new ExcecoesCustomizada("Cliente não encontrado", HttpStatus.NOT_FOUND));
        }

        Produto produto = produtoRepository.findById(dto.idProduto()).orElseThrow(() -> new ExcecoesCustomizada("Produto não encontrado", HttpStatus.NOT_FOUND));
        Plano plano = produto.getPlanos().stream()
                .filter(p -> p.getId().equals(dto.idPlano()))
                .findFirst()
                .orElseThrow(() -> new ExcecoesCustomizada("Plano não encontrado!", HttpStatus.BAD_REQUEST));

        if (dto.idCupomUsado() != null && dto.idCupomUsado() > 0) {
            cupom = produto.getCupom().stream()
                    .filter(c -> c.getId().equals(dto.idCupomUsado()))
                    .findFirst()
                    .orElseThrow(() -> new ExcecoesCustomizada("Cupom não encontrado ou inválido", HttpStatus.BAD_REQUEST));

        }

        venda.setValorPago(dto.valorPago());
        venda.setCupomUsado(cupom);
        venda.setOrigemCompra(dto.origemCompra());
        venda.setPlano(plano);
        venda.setMetodoPagamento(dto.metodoPagamento());
        venda.setStatusPagamento(dto.statusPagamento());
        venda.setStatusVenda(dto.statusVenda());
        venda.setTipoRecorrencia(dto.tipoRecorrencia());
        venda.setCliente(cliente);
        venda.setVendedor(vendedor);
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
