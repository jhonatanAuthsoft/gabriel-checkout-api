package com.projeto.modelo.controller.dto.response.cielo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CieloResponseCallback(
        @JsonProperty("MerchantOrderId") String idPedidoLoja,
        @JsonProperty("AcquirerOrderId") String AcquirerOrderId,
        @JsonProperty("Customer") CieloClienteResponse cliente,
        @JsonProperty("Payment") CieloPagamentoResponseConsulta pagamento,
        @JsonProperty("Merchant") CieloComercianteResponse comerciante
) {
}
