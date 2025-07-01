package com.projeto.modelo.mapper;

import com.projeto.modelo.model.entity.Plano;
import com.projeto.modelo.model.entity.Produto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


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
} 