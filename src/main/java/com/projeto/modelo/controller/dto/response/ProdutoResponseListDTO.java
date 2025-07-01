package com.projeto.modelo.controller.dto.response;

import com.projeto.modelo.model.enums.ProdutoStatus;
import com.projeto.modelo.model.enums.UsuarioStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProdutoResponseListDTO(
        Long id,
        String nome,
        String codigo,
        BigDecimal valor,
        Long afiliados,
        Long vendasTotais,
        ProdutoStatus status
) {
}
