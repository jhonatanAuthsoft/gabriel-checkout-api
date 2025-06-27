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
public class BancoInterPixResponseDTO {
    private BancoInterCalendarioResponseDTO calendario;
    private String txid;
    private int revisao;
    private BancoInterLocResponseDTO loc;
    private String location;
    private String status;
    private BancoInterDevedorResponseDTO devedor;
    private BancoInterValorResponseDTO valor;
    private String chave;
    private String solicitacaoPagador;
    private String pixCopiaECola;

}
