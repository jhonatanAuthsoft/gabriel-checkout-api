package com.projeto.modelo.controller.dto.response.dashboard;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProdutosMaisVendidos(
        Long idProduto,
        String nome,
        Long totalVendas,
        BigDecimal percentualDasVendas
) {}
