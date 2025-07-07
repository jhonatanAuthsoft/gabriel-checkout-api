package com.projeto.modelo.controller.dto.response.cielo;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CieloLinkResponse(
        @JsonProperty("Method") String metodo,
        @JsonProperty("Rel") String relacao,
        @JsonProperty("Href") String url
) {
}
