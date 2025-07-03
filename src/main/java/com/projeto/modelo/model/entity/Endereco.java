package com.projeto.modelo.model.entity;

import com.projeto.modelo.model.enums.Uf;
import lombok.Builder;

@Builder
public record Endereco(
        String endereco,
        String numeroResidencia,
        String complementoEndereco,
        String bairro,
        String cidade,
        Uf uf,
        String cep
) {
}
