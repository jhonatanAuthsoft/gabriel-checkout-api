package com.projeto.modelo.controller.dto.response;

import com.projeto.modelo.model.entity.Produto;
import lombok.Builder;

@Builder
public record ProdutoResponseDTO(Produto dados) {

} 