package com.projeto.modelo.controller.dto.request.cielo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record Endereco(
        @JsonProperty("Street") String logradouro,
        @JsonProperty("Number") String numero,
        @JsonProperty("Complement") String complemento,
        @JsonProperty("ZipCode") String cep,
        @JsonProperty("City") String cidade,
        @JsonProperty("State") String estado,
        @JsonProperty("Country") String pais,
        @JsonProperty("AddressType") Integer tipoEndereco

) {
}
