package com.projeto.modelo.mapper;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.model.entity.Plano;
import com.projeto.modelo.model.entity.Produto;
import com.projeto.modelo.model.entity.ProdutoUpsell;
import com.projeto.modelo.model.enums.ProdutoStatus;
import com.projeto.modelo.repository.ProdutoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ProdutoUpsellMapper {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<ProdutoUpsell> toEntity(List<ProdutoUpsell> recebidos, Produto produtoOrigem) {
        List<ProdutoUpsell> resultado = new ArrayList<>();

        for (ProdutoUpsell dto : recebidos) {
            if (dto.getProduto() == null || dto.getProduto().getId() == null ||
                dto.getPlano() == null || dto.getPlano().getId() == null) {
                throw new ExcecoesCustomizada("Produto e Plano são obrigatórios em cada upsell", HttpStatus.BAD_REQUEST);
            }

            Produto produto = produtoRepository.findById(dto.getProduto().getId())
                    .orElseThrow(() -> new ExcecoesCustomizada("Produto não encontrado", HttpStatus.NOT_FOUND));
            Plano plano = produto.getPlanos().stream().filter(p -> dto.getPlano().getId().equals(p.getId()))
                    .findFirst()
                    .orElseThrow(() -> new ExcecoesCustomizada("Plano não encontrado", HttpStatus.NOT_FOUND));

            ProdutoUpsell upsell = ProdutoUpsell.builder()
                    .produto(produto)
                    .produtoOrigem(produtoOrigem)
                    .plano(plano)
                    .build();

            resultado.add(upsell);
        }

        return resultado;
    }

    public void editarProdutoUpsell(List<ProdutoUpsell> recebidos, Produto produto) {
        List<ProdutoUpsell> existentes = produto.getProdutosUpsell(); // lista atual
        Map<String, ProdutoUpsell> existentesPorChave = existentes.stream()
                .filter(pu -> pu.getProduto() != null && pu.getPlano() != null)
                .collect(Collectors.toMap(
                        pu -> pu.getProduto().getId() + "-" + pu.getPlano().getId(),
                        pu -> pu
                ));

        Set<String> chavesRecebidas = new HashSet<>();

        for (ProdutoUpsell dto : recebidos) {
            Long idProduto = dto.getProduto().getId();
            Long idPlano = dto.getPlano().getId();
            if (idProduto == null || idPlano == null) continue;

            Produto produtoRelacionado = produtoRepository.findById(idProduto)
                    .orElseThrow(() -> new ExcecoesCustomizada("Produto não encontrado", HttpStatus.NOT_FOUND));
            Plano planoRelacionado = produtoRelacionado.getPlanos().stream().filter(p -> dto.getPlano().getId().equals(p.getId()))
                    .findFirst()
                    .orElseThrow(() -> new ExcecoesCustomizada("Plano não encontrado", HttpStatus.NOT_FOUND));

            String chave = idProduto + "-" + idPlano;
            chavesRecebidas.add(chave);

            if (!existentesPorChave.containsKey(chave)) {
                ProdutoUpsell novo = ProdutoUpsell.builder()
                        .produto(produtoRelacionado)
                        .produtoOrigem(produto)
                        .plano(planoRelacionado)
                        .build();
                existentes.add(novo);
            }
        }

        // Remover os upsells antigos que não estão na nova lista
        List<ProdutoUpsell> paraRemover = existentes.stream()
                .filter(pu -> {
                    String chave = pu.getProduto().getId() + "-" + pu.getPlano().getId();
                    return !chavesRecebidas.contains(chave);
                })
                .toList();

        existentes.removeAll(paraRemover);
    }


}
