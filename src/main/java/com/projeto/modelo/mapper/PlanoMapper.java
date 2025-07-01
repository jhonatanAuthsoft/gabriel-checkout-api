package com.projeto.modelo.mapper;

import com.projeto.modelo.model.entity.Plano;
import com.projeto.modelo.model.entity.Produto;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class PlanoMapper {
    public List<Plano> toEntity(List<Plano> plano, Produto produto) {
        List<Plano> planos = new ArrayList<>();

        for (Plano planoRecebido : plano) {
            planos.add(Plano.builder()
                    .nome(planoRecebido.getNome())
                    .peridiocidade(planoRecebido.getPeridiocidade())
                    .descricao(planoRecebido.getDescricao())
                    .preco(planoRecebido.getPreco())
                    .gratis(planoRecebido.getGratis())
                    .primeiraParcela(planoRecebido.getPrimeiraParcela())
                    .recorrencia(planoRecebido.getRecorrencia())
                    .sku(planoRecebido.getSku())
                    .produto(produto)
                    .build());
        }

        return planos;
    }

    public void editarPlano(List<Plano> novos, Produto produto) {
        List<Plano> existentes = produto.getPlanos();

        Map<Long, Plano> existentesPorId = existentes.stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(Plano::getId, p -> p));

        // IDs recebidos no DTO
        Set<Long> idsDto = new HashSet<>();

        for (Plano dto : novos) {
            if (dto.getId() != null && existentesPorId.containsKey(dto.getId())) {
                Plano plano = existentesPorId.get(dto.getId());
                idsDto.add(plano.getId());

                plano.setNome(dto.getNome());
                plano.setDescricao(dto.getDescricao());
                plano.setPreco(dto.getPreco());
                plano.setGratis(dto.getGratis());
                plano.setPeridiocidade(dto.getPeridiocidade());
                plano.setPrimeiraParcela(dto.getPrimeiraParcela());
                plano.setRecorrencia(dto.getRecorrencia());
                plano.setSku(dto.getSku());
            } else {
                Plano novo = new Plano();
                novo.setProduto(produto);
                novo.setNome(dto.getNome());
                novo.setDescricao(dto.getDescricao());
                novo.setPreco(dto.getPreco());
                novo.setGratis(dto.getGratis());
                novo.setPeridiocidade(dto.getPeridiocidade());
                novo.setPrimeiraParcela(dto.getPrimeiraParcela());
                novo.setRecorrencia(dto.getRecorrencia());
                novo.setSku(dto.getSku());

                existentes.add(novo);
            }
        }

        // Remover os que não estão mais na lista recebida
        List<Plano> paraRemover = existentes.stream()
                .filter(p -> p.getId() != null && !idsDto.contains(p.getId()))
                .toList();

        existentes.removeAll(paraRemover);
    }



} 