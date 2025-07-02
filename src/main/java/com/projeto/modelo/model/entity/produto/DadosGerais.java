package com.projeto.modelo.model.entity.produto;

import jakarta.persistence.Column;
import lombok.Builder;

@Builder
public record DadosGerais(
    String codigo,
    String chave,
    String nome,
    @Column(name = "codigo_sku")
    String codigoSku,
    String descricao
) {} 