package com.projeto.modelo.mapper;

import com.projeto.modelo.model.entity.CheckoutProduto;
import com.projeto.modelo.model.entity.Pergunta;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PerguntaMapper {
    public List<Pergunta> toEntity(List<Pergunta> perguntas, CheckoutProduto checkoutProduto) {
        List<Pergunta> perguntasMapeadas = new ArrayList<>();

        for (Pergunta pergunta : perguntas) {
            perguntasMapeadas.add(Pergunta.builder()
                    .pergunta(pergunta.getPergunta())
                    .resposta(pergunta.getResposta())
                    .checkoutProduto(checkoutProduto)
                    .build());
        }

        return perguntasMapeadas;
    }

    public void editarPerguntas(List<Pergunta> novasPerguntas, CheckoutProduto checkoutProduto) {
        List<Pergunta> existentes = checkoutProduto.getPerguntas();

        Map<Long, Pergunta> existentesPorId = existentes.stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(Pergunta::getId, p -> p));

        Set<Long> idsDto = new HashSet<>();

        for (Pergunta dto : novasPerguntas) {
            if (dto.getId() != null && existentesPorId.containsKey(dto.getId())) {
                Pergunta pergunta = existentesPorId.get(dto.getId());
                idsDto.add(pergunta.getId());

                pergunta.setPergunta(dto.getPergunta());
                pergunta.setResposta(dto.getResposta());

            } else {
                Pergunta nova = Pergunta.builder()
                        .pergunta(dto.getPergunta())
                        .resposta(dto.getResposta())
                        .checkoutProduto(checkoutProduto)
                        .build();

                existentes.add(nova);
            }
        }

        // Remove perguntas que não vieram no DTO
        List<Pergunta> paraRemover = existentes.stream()
                .filter(p -> p.getId() != null && !idsDto.contains(p.getId()))
                .toList();

        existentes.removeAll(paraRemover);
    }


}