package com.projeto.modelo.model.entity.produto;

import com.projeto.modelo.model.enums.TipoImagem;
import lombok.Builder;

@Builder
public record MapeamentoImagem(
        String nomeArquivo,
        TipoImagem tipo
) {
}