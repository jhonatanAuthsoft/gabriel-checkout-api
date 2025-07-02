package com.projeto.modelo.model.entity.produto;

import jakarta.persistence.Column;
import lombok.Builder;

@Builder
public record Exibicoes(
        @Column(name = "exibir_imagems_produto")
        Boolean exibirImagensProduto,
        Boolean exibirSelos,
        Boolean exibirFaq
) {
}