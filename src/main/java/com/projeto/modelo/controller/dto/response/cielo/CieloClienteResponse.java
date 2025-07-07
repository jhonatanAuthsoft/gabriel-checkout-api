package com.projeto.modelo.controller.dto.response.cielo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projeto.modelo.controller.dto.request.cielo.Endereco;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CieloClienteResponse(
        @JsonProperty("Name") String nome,
        @JsonProperty("Identity") String documento,
        @JsonProperty("IdentityType") String tipoDocumento,
        @JsonProperty("Email") String email,
        @JsonProperty("Birthdate") String dataNascimento,
        @JsonProperty("Address") Endereco endereco,
        @JsonProperty("DeliveryAddress") Endereco enderecoEntrega
) {
}
