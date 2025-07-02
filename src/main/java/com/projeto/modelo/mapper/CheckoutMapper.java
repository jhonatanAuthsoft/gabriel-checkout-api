package com.projeto.modelo.mapper;

import com.projeto.modelo.model.entity.CheckoutProduto;
import com.projeto.modelo.model.entity.Pergunta;
import com.projeto.modelo.model.entity.Produto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
public class CheckoutMapper {

    @Autowired
    private PerguntaMapper perguntaMapper;

    public CheckoutProduto toEntity(CheckoutProduto checkoutProduto, Produto produto) {
        checkoutProduto.setProduto(produto);
        checkoutProduto.setPerguntas(perguntaMapper.toEntity(checkoutProduto.getPerguntas(), checkoutProduto));

        return checkoutProduto;
    }

    public void editarCheckout(CheckoutProduto checkoutProduto, List<Pergunta> perguntas) {
        perguntaMapper.editarPerguntas(perguntas, checkoutProduto);
    }
} 