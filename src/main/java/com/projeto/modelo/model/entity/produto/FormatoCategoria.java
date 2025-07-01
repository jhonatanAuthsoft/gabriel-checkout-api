package com.projeto.modelo.model.entity.produto;

import lombok.Builder;

@Builder
public record FormatoCategoria(
    String formato,
    String categoria
) {} 