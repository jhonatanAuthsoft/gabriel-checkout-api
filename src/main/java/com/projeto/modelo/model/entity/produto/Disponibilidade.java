package com.projeto.modelo.model.entity.produto;

import jakarta.persistence.Column;
import lombok.Builder;

@Builder
public record Disponibilidade(
    Boolean disponivel,
    @Column(name = "quantidade_maxima")
    Long quantidadeMaxima
) {} 