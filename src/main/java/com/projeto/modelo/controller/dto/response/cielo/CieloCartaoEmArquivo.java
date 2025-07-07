package com.projeto.modelo.controller.dto.response.cielo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CieloCartaoEmArquivo(
        @JsonProperty("Usage") String uso,
        @JsonProperty("Reason") String motivo
) {
}
