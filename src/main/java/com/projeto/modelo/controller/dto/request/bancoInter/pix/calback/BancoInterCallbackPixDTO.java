package com.projeto.modelo.controller.dto.request.bancoInter.pix.calback;


import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record BancoInterCallbackPixDTO (
        String endToEndId,
        String chave,
        BancoInterComponentesValorDTO componentesValor,
        String txid,
        BigDecimal valor,
        OffsetDateTime horario,
        String infoPagador,
        BancoInterPagador pagador
) {}

