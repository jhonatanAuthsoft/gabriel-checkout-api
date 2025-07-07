package com.projeto.modelo.controller.dto.request.cielo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.projeto.modelo.model.enums.TipoIdentificador;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CieloProprietario(
        String nome,
        String status,
        String cpfCnpjRg,
        TipoIdentificador tipoIdentificador,
        String email,
        String aniversario,
        Endereco enderecoProprietario,
        Endereco enderecoEntrega,
        Endereco enderecoCobranca
) {
}
