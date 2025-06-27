package com.projeto.modelo.controller.dto.response.bancoInter.pix;

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
public class BancoInterLocResponseDTO {
    private int id;
    private String location;
    private String tipoCob;
    private String criacao;

}
