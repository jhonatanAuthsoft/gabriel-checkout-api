package com.projeto.modelo.controller.dto.request.bancoInter.boleto;

import java.time.OffsetDateTime;

public record BancoInterCallbackBoletoDTO(
        String codigoSolicitacao,
        String seuNumero,
        String situacao,
        OffsetDateTime dataHoraSituacao,
        String valorTotalRecebido,
        String origemRecebimento,
        String nossoNumero,
        String codigoBarras,
        String linhaDigitavel,
        String txid,
        String pixCopiaECola
) {}
