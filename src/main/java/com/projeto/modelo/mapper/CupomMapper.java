package com.projeto.modelo.mapper;

import com.projeto.modelo.model.entity.Cupom;
import com.projeto.modelo.model.entity.Produto;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class CupomMapper {
    public List<Cupom> toEntity(List<Cupom> cupom, Produto produto) {
        List<Cupom> cupoms = new ArrayList<>();

        for (Cupom cupomRecebido : cupom) {
            cupoms.add(Cupom.builder()
                    .codigoCupom(cupomRecebido.getCodigoCupom())
                    .tipoDesconto(cupomRecebido.getTipoDesconto())
                    .valor(cupomRecebido.getValor())
                    .url(cupomRecebido.getUrl())
                    .produto(produto)
                    .build());
        }

        return cupoms;
    }

    public void editarCupom(List<Cupom> novosCupons, Produto produto) {
        List<Cupom> existentes = produto.getCupom(); // mantém a referência viva

        Map<Long, Cupom> existentesPorId = existentes.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(Cupom::getId, c -> c));

        Set<Long> idsDto = new HashSet<>();

        for (Cupom dto : novosCupons) {
            if (dto.getId() != null && existentesPorId.containsKey(dto.getId())) {
                // Atualiza entidade já gerenciada
                Cupom cupom = existentesPorId.get(dto.getId());
                idsDto.add(cupom.getId());

                cupom.setCodigoCupom(dto.getCodigoCupom());
                cupom.setTipoDesconto(dto.getTipoDesconto());
                cupom.setValor(dto.getValor());
                cupom.setUrl(dto.getUrl());

            } else {
                // Cria novo cupom
                Cupom novo = new Cupom();
                novo.setProduto(produto);
                novo.setCodigoCupom(dto.getCodigoCupom());
                novo.setTipoDesconto(dto.getTipoDesconto());
                novo.setValor(dto.getValor());
                novo.setUrl(dto.getUrl());

                existentes.add(novo);
            }
        }

        // Remove cupons antigos que não estão na nova lista
        List<Cupom> paraRemover = existentes.stream()
                .filter(c -> c.getId() != null && !idsDto.contains(c.getId()))
                .toList();

        existentes.removeAll(paraRemover);
    }


}