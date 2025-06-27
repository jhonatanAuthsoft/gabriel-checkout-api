package com.projeto.modelo.controller.dto.response.bancoInter.boleto;

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
public class BancoInterDescontoResponseDTO {
    private String codigo;
    private Integer quantidadeDias;
    private Double taxa;

}
