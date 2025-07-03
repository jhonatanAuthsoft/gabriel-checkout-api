package com.projeto.modelo.controller.dto.request;

import com.projeto.modelo.model.enums.*;

import java.math.BigDecimal;

public record AtualizarPedidoAdmin(
        Long idProduto,
        BigDecimal valorPago,
        Long idCupomUsado,
        Long idPlano,
        OrigemCompra origemCompra,
        MetodoPagamento metodoPagamento,
        StatusPagamento statusPagamento,
        StatusVenda statusVenda,
        TipoCobranca tipoRecorrencia,
        Long idCliente,
        Long idVendedor
) {
}
