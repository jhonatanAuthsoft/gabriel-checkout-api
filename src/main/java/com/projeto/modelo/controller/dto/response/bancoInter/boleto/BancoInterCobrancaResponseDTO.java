package com.projeto.modelo.controller.dto.response.bancoInter.boleto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BancoInterCobrancaResponseDTO {

    private String codigoSolicitacao;
    private String seuNumero;
    private String dataEmissao;
    private String dataVencimento;
    private Double valorNominal;
    private String tipoCobranca;
    private String situacao;
    private String dataSituacao;
    private String valorTotalRecebido;
    private String origemRecebimento;
    private Boolean arquivada;
    private List<BancoInterDescontoResponseDTO> descontos;
    private BancoInterMultaResponseDTO multa;
    private BancoInterMoraResponseDTO mora;
    private BancoInterPagadorResponseDTO pagador;
}
