package com.projeto.modelo.controller.dto.response.cielo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CieloCartaoResponse(
        @JsonProperty("CardNumber") String numeroCartao,
        @JsonProperty("Holder") String nomeTitular,
        @JsonProperty("ExpirationDate") String dataValidade,
        @JsonProperty("SaveCard") Boolean salvarCartao,
        @JsonProperty("Brand") String bandeira,
        @JsonProperty("CardOnFile") CieloCartaoEmArquivo cartaoEmArquivo,
        @JsonProperty("PaymentAccountReference") String referenciaContaPagamento
) {
}
