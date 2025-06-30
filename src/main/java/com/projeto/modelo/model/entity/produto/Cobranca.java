package com.projeto.modelo.model.entity.produto;

import com.projeto.modelo.model.enums.TipoPrimeiraParcela;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Cobranca(
        @Column(name = "tipo_cobranca")
        String tipoCobranca,
        String peridiocidade,
        BigDecimal preco,
        Boolean gratis,
        @Enumerated(EnumType.STRING)
        @Column(name = "tipo_primeira_parcela")
        TipoPrimeiraParcela tipoPrimeiraParcela,
        @Column(name = "valor_primeira_parcela")
        BigDecimal valorPrimeiraParcela,
        Long carencia
) {
}