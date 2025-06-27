package com.projeto.modelo.controller.dto.response.bancoInter.pix;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BancoInterDevedorResponseDTO {
    @JsonAlias({"cnpj", "cpf"})
    private String cpfCnpj;
    private String nome;
}
