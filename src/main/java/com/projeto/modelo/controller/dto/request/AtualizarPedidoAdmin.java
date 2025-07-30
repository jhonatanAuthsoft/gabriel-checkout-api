package com.projeto.modelo.controller.dto.request;

import com.projeto.modelo.model.enums.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AtualizarPedidoAdmin(
        List<Long> idsProduto,
        BigDecimal valorPago,
        Long idCupomUsado,
        List<Long> idsPlano,
        OrigemCompra origemCompra,
        MetodoPagamento metodoPagamento,
        StatusPagamento statusPagamento,
        StatusVenda statusVenda,
        TipoCobranca tipoRecorrencia,
        Long idCliente,
        Long idVendedor,
        LocalDateTime dataReembolso
) {
}
