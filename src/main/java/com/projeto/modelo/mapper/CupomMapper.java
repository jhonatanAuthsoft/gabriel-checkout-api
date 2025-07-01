package com.projeto.modelo.mapper;

import com.projeto.modelo.model.entity.Cupom;
import com.projeto.modelo.model.entity.Produto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


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
} 