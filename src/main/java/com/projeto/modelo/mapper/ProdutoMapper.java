package com.projeto.modelo.mapper;

import com.projeto.modelo.controller.dto.request.CadastrarProdutoDTO;
import com.projeto.modelo.controller.dto.response.ProdutoResponseDTO;
import com.projeto.modelo.controller.dto.response.ProdutoResponseListDTO;
import com.projeto.modelo.model.entity.Imagem;
import com.projeto.modelo.model.entity.Produto;
import com.projeto.modelo.model.entity.produto.*;
import com.projeto.modelo.model.enums.ProdutoStatus;
import com.projeto.modelo.service.AwsS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProdutoMapper {

    @Autowired
    private CheckoutMapper checkoutMapper;

    @Autowired
    private PlanoMapper planoMapper;

    @Autowired
    private CupomMapper cupomMapper;

    @Autowired
    private AwsS3Service awsS3Service;

    public ProdutoResponseDTO toResponseDTO(Produto produto) {

        List<Imagem> imagens = new ArrayList<>();

        if (produto.getImagens() != null && !produto.getImagens().isEmpty()) {
            imagens = produto.getImagens().stream()
                    .peek(imagem -> imagem.setSignedUrl(awsS3Service.generateSignedDownloadUrl(imagem.getId())))
                    .toList();
        }

        return ProdutoResponseDTO.builder()
                .dados(Produto.builder()
                        .id(produto.getId())
                        .status(produto.getStatus())
                        .dadosProduto(produto.getDadosProduto())
                        .checkoutProduto(produto.getCheckoutProduto())
                        .planos(produto.getPlanos())
                        .cupom(produto.getCupom())
                        .imagens(imagens)
                        .dataCriacao(produto.getDataCriacao())
                        .dataAtualizacao(produto.getDataAtualizacao())
                        .build())
                .build();
    }

    private ProdutoStatus definirStatusProduto(Disponibilidade disponibilidade, Long vendasTotais) {
        if (!disponibilidade.disponivel()) {
            return ProdutoStatus.INATIVO;
        }

        if (vendasTotais >= disponibilidade.quantidadeMaxima()) {
            return ProdutoStatus.ESGOTADO;
        }

        return ProdutoStatus.ATIVO;
    }

    public Page<ProdutoResponseListDTO> toResponseListDTO(Page<Produto> produtos) {

        Long vendasTotais = 0L;
//        Long afiliadosTotais;

        return produtos.map(produto -> ProdutoResponseListDTO.builder()
                .id(produto.getId())
                .nome(produto.getDadosProduto().dadosGerais().nome())
                .codigo(produto.getDadosProduto().dadosGerais().codigo())
                .valor(produto.getDadosProduto().cobranca().preco())
                //.afiliados(produto.getAfiliados()) Isso aqui será a quantidade de funcionários? eu devo associar ou não de onde vem?
                //.vendasTotais(produto.getVendasTotais()) Fazer um getAllByProduto, na entidade de vendas
                .status(definirStatusProduto(produto.getDadosProduto().disponibilidade(), vendasTotais))
                .build()
        );
    }

    public Produto toEntity(CadastrarProdutoDTO dto) throws IOException {
        Produto produto = Produto.builder()
                .status(ProdutoStatus.ATIVO)
                .dadosProduto(DadosProduto.builder()
                        .dadosGerais(DadosGerais.builder()
                                .codigo(dto.dados().getDadosProduto().dadosGerais().codigo())
                                .chave(dto.dados().getDadosProduto().dadosGerais().chave())
                                .nome(dto.dados().getDadosProduto().dadosGerais().nome())
                                .codigoSku(dto.dados().getDadosProduto().dadosGerais().codigoSku())
                                .descricao(dto.dados().getDadosProduto().dadosGerais().descricao())
                                .build())
                        .cobranca(Cobranca.builder()
                                .tipoCobranca(dto.dados().getDadosProduto().cobranca().tipoCobranca())
                                .peridiocidade(dto.dados().getDadosProduto().cobranca().peridiocidade())
                                .preco(dto.dados().getDadosProduto().cobranca().preco())
                                .gratis(dto.dados().getDadosProduto().cobranca().gratis())
                                .tipoPrimeiraParcela(dto.dados().getDadosProduto().cobranca().tipoPrimeiraParcela())
                                .valorPrimeiraParcela(dto.dados().getDadosProduto().cobranca().valorPrimeiraParcela())
                                .carencia(dto.dados().getDadosProduto().cobranca().carencia())
                                .build())
                        .disponibilidade(Disponibilidade.builder()
                                .disponivel(dto.dados().getDadosProduto().disponibilidade().disponivel())
                                .quantidadeMaxima(dto.dados().getDadosProduto().disponibilidade().quantidadeMaxima())
                                .build())
                        .formatoCategoria(FormatoCategoria.builder()
                                .formato(dto.dados().getDadosProduto().formatoCategoria().formato())
                                .categoria(dto.dados().getDadosProduto().formatoCategoria().categoria())
                                .build())
                        .suporteGarantia(SuporteGarantia.builder()
                                .email(dto.dados().getDadosProduto().suporteGarantia().email())
                                .telefoneSuporte(dto.dados().getDadosProduto().suporteGarantia().telefoneSuporte())
                                .mostrarTelefoneSuporte(dto.dados().getDadosProduto().suporteGarantia().mostrarTelefoneSuporte())
                                .whatsappSuporte(dto.dados().getDadosProduto().suporteGarantia().whatsappSuporte())
                                .mostrarWhatsappSuporte(dto.dados().getDadosProduto().suporteGarantia().mostrarWhatsappSuporte())
                                .build())
                        .urlPersonalizada(dto.dados().getDadosProduto().urlPersonalizada())
                        .build())
                .build();

        produto.setCheckoutProduto(checkoutMapper.toEntity(dto.dados().getCheckoutProduto(), produto));
        produto.setPlanos(planoMapper.toEntity(dto.dados().getPlanos(), produto));
        produto.setCupom(cupomMapper.toEntity(dto.dados().getCupom(), produto));

        return produto;
    }

    public void editarProduto(Produto produto, CadastrarProdutoDTO dto) {
        produto.setStatus(ProdutoStatus.ATIVO);

        DadosProduto dadosProduto = DadosProduto.builder()
                .dadosGerais(DadosGerais.builder()
                        .codigo(dto.dados().getDadosProduto().dadosGerais().codigo())
                        .chave(dto.dados().getDadosProduto().dadosGerais().chave())
                        .nome(dto.dados().getDadosProduto().dadosGerais().nome())
                        .codigoSku(dto.dados().getDadosProduto().dadosGerais().codigoSku())
                        .descricao(dto.dados().getDadosProduto().dadosGerais().descricao())
                        .build())
                .cobranca(Cobranca.builder()
                        .tipoCobranca(dto.dados().getDadosProduto().cobranca().tipoCobranca())
                        .peridiocidade(dto.dados().getDadosProduto().cobranca().peridiocidade())
                        .preco(dto.dados().getDadosProduto().cobranca().preco())
                        .gratis(dto.dados().getDadosProduto().cobranca().gratis())
                        .tipoPrimeiraParcela(dto.dados().getDadosProduto().cobranca().tipoPrimeiraParcela())
                        .valorPrimeiraParcela(dto.dados().getDadosProduto().cobranca().valorPrimeiraParcela())
                        .carencia(dto.dados().getDadosProduto().cobranca().carencia())
                        .build())
                .disponibilidade(Disponibilidade.builder()
                        .disponivel(dto.dados().getDadosProduto().disponibilidade().disponivel())
                        .quantidadeMaxima(dto.dados().getDadosProduto().disponibilidade().quantidadeMaxima())
                        .build())
                .formatoCategoria(FormatoCategoria.builder()
                        .formato(dto.dados().getDadosProduto().formatoCategoria().formato())
                        .categoria(dto.dados().getDadosProduto().formatoCategoria().categoria())
                        .build())
                .suporteGarantia(SuporteGarantia.builder()
                        .email(dto.dados().getDadosProduto().suporteGarantia().email())
                        .telefoneSuporte(dto.dados().getDadosProduto().suporteGarantia().telefoneSuporte())
                        .mostrarTelefoneSuporte(dto.dados().getDadosProduto().suporteGarantia().mostrarTelefoneSuporte())
                        .whatsappSuporte(dto.dados().getDadosProduto().suporteGarantia().whatsappSuporte())
                        .mostrarWhatsappSuporte(dto.dados().getDadosProduto().suporteGarantia().mostrarWhatsappSuporte())
                        .build())
                .urlPersonalizada(dto.dados().getDadosProduto().urlPersonalizada())
                .build();

        produto.setDadosProduto(dadosProduto);

        checkoutMapper.editarCheckout(produto.getCheckoutProduto(), dto.dados().getCheckoutProduto().getPerguntas());
        planoMapper.editarPlano(dto.dados().getPlanos(), produto);
        cupomMapper.editarCupom(dto.dados().getCupom(), produto);

    }
}
