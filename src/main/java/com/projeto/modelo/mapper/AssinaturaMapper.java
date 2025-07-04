package com.projeto.modelo.mapper;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.controller.dto.request.AssinaturaRequestDTO;
import com.projeto.modelo.controller.dto.response.AssinaturaResponseDTO;
import com.projeto.modelo.model.entity.*;
import com.projeto.modelo.repository.ProdutoRepository;
import com.projeto.modelo.repository.UsuarioRepository;
import com.projeto.modelo.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AssinaturaMapper {

    @Autowired
    VendaRepository vendaRepository;

    @Autowired
    ProdutoRepository produtoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    public Assinatura toEntity(AssinaturaRequestDTO dto) {
        if (dto.idVenda() == null)
            throw new ExcecoesCustomizada("Existem dados obrigatórios não presentes!", HttpStatus.BAD_REQUEST);

        Venda venda = vendaRepository.findById(dto.idVenda()).orElseThrow(() -> new ExcecoesCustomizada("Venda não encontrada!", HttpStatus.NOT_FOUND));

        if (venda.getDataPagamento() == null)
            throw new ExcecoesCustomizada("Venda ainda não concluída, não é possível criar assinatura com essa venda!", HttpStatus.BAD_REQUEST);

        LocalDateTime dataFim;
        LocalDateTime dataPagamento = venda.getDataPagamento();

        dataFim = switch (venda.getPlano().getPeridiocidade()) {
            case MENSAL -> dataPagamento.plusMonths(1);
            case BIMESTRAL -> dataPagamento.plusMonths(2);
            case TRIMESTRAL -> dataPagamento.plusMonths(3);
            case SEMESTRAL -> dataPagamento.plusMonths(6);
            case ANUAL -> dataPagamento.plusYears(1);
        };

        return Assinatura.builder()
                .produto(venda.getProduto())
                .plano(venda.getPlano())
                .cliente(venda.getCliente())
                .venda(venda)
                .tipoCobranca(venda.getTipoRecorrencia())
                .metodoPagamento(venda.getMetodoPagamento())
                .statusAssinatura(dto.statusAssinatura())
                .dataInicio(dataPagamento)
                .dataFim(dataFim)
                .build();
    }

    public void atualizarAssinatura(Assinatura assinatura, AssinaturaRequestDTO dto) {
        if (dto.idPlano() == null || dto.idProduto() == null || dto.idVenda() == null || dto.idUsuario() == null)
            throw new ExcecoesCustomizada("Existem dados obrigatórios não presentes!", HttpStatus.BAD_REQUEST);

        Venda venda = vendaRepository.findById(dto.idVenda()).orElseThrow(() -> new ExcecoesCustomizada("Venda não encontrada!", HttpStatus.NOT_FOUND));
        Produto produto = produtoRepository.findById(dto.idProduto()).orElseThrow(() -> new ExcecoesCustomizada("Produto não encontrado!", HttpStatus.NOT_FOUND));
        Plano plano = produto.getPlanos()
                .stream()
                .filter(p -> p.getId().equals(dto.idPlano()))
                .findFirst()
                .orElseThrow(() -> new ExcecoesCustomizada("Plano não encontrado!", HttpStatus.NOT_FOUND));
        Usuario cliente = usuarioRepository.findById(dto.idUsuario()).orElseThrow(() -> new ExcecoesCustomizada("Cliente não encontrado!", HttpStatus.NOT_FOUND));

        assinatura.setProduto(produto);
        assinatura.setPlano(plano);
        assinatura.setVenda(venda);
        assinatura.setCliente(cliente);
        assinatura.setTipoCobranca(dto.tipoCobranca());
        assinatura.setMetodoPagamento(dto.metodoPagamento());
        assinatura.setStatusAssinatura(dto.statusAssinatura());
        assinatura.setDataInicio(dto.dataInicio());
        assinatura.setDataFim(dto.dataFim());
        assinatura.setDataCancelamentoDatado(dto.dataCancelamentoDatado());
    }

    public AssinaturaResponseDTO toResponseDTO(Assinatura assinatura) {
        return AssinaturaResponseDTO.builder()
                .id(assinatura.getId())
                .produto(assinatura.getProduto())
                .plano(assinatura.getPlano())
                .cliente(assinatura.getCliente())
                .venda(assinatura.getVenda())
                .tipoCobranca(assinatura.getTipoCobranca())
                .metodoPagamento(assinatura.getMetodoPagamento())
                .statusAssinatura(assinatura.getStatusAssinatura())
                .dataInicio(assinatura.getDataInicio())
                .dataFim(assinatura.getDataFim())
                .dataCancelamentoDatado(assinatura.getDataCancelamentoDatado())
                .build();
    }

    public Page<AssinaturaResponseDTO> toResponseDTOList(Page<Assinatura> assinaturas) {
        return assinaturas.map(this::toResponseDTO);
    }
}
