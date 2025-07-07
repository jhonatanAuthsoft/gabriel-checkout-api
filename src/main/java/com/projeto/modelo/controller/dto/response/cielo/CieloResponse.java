package com.projeto.modelo.controller.dto.response.cielo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record CieloResponse(
        @JsonProperty("MerchantOrderId") String idPedidoLoja,
        @JsonProperty("Customer") CieloClienteResponse cliente,
        @JsonProperty("Payment") CieloPagamentoResponse pagamento
) {
}
