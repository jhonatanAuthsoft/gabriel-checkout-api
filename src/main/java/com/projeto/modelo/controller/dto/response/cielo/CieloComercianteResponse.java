package com.projeto.modelo.controller.dto.response.cielo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CieloComercianteResponse(
        String id,
        @JsonProperty("TradeName")
        String nome
) {
}
