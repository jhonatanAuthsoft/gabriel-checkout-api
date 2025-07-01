package com.projeto.modelo.mapper;

import com.projeto.modelo.model.entity.Checkout;
import com.projeto.modelo.model.entity.Pergunta;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PerguntaMapper {
    public List<Pergunta> toEntity(List<Pergunta> perguntas, Checkout checkout) {
        List<Pergunta> perguntasMapeadas = new ArrayList<>();

        for (Pergunta pergunta : perguntas) {
            perguntasMapeadas.add(Pergunta.builder()
                    .pergunta(pergunta.getPergunta())
                    .resposta(pergunta.getResposta())
                    .checkout(checkout)
                    .build());
        }

        return perguntasMapeadas;
    }
} 