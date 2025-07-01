package com.projeto.modelo.mapper;

import com.projeto.modelo.model.entity.Checkout;
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

    public Checkout toEntity(Checkout checkout, Produto produto) {
        checkout.setProduto(produto);
        checkout.setPerguntas(perguntaMapper.toEntity(checkout.getPerguntas(), checkout));

        return checkout;
    }

    public void editarCheckout(Checkout checkout, List<Pergunta> perguntas) {
        perguntaMapper.editarPerguntas(perguntas, checkout);
    }
} 