package com.projeto.modelo.model.entity.produto;

import lombok.Builder;

@Builder
public record MapeamentoImagem(
        String nomeArquivo,
        String tipo
) {
}