package com.projeto.modelo.controller.dto.response.cielo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CieloPagamentoResponse(
        @JsonProperty("ServiceTaxAmount") Integer valorTaxaServico,
        @JsonProperty("Installments") Integer parcelas,
        @JsonProperty("Interest") Integer tipoJuros,
        @JsonProperty("Capture") Boolean capturarAutomaticamente,
        @JsonProperty("Authenticate") Boolean autenticar,
        @JsonProperty("Recurrent") Boolean recorrente,
        @JsonProperty("CreditCard") CieloCartaoResponse cartaoCredito,
        @JsonProperty("DebitCard") CieloCartaoResponse cartaoDebito,
        @JsonProperty("Tid") String tid,
        @JsonProperty("ProofOfSale") String comprovanteVenda,
        @JsonProperty("AuthorizationCode") String codigoAutorizacao,
        @JsonProperty("SoftDescriptor") String descricaoFatura,
        @JsonProperty("Provider") String provedor,
        @JsonProperty("IsQrCode") Boolean ehQrCode,
        @JsonProperty("Amount") Integer valorCentavos,
        @JsonProperty("ReceivedDate") String dataRecebimento,
        @JsonProperty("CapturedAmount") Integer valorCapturado,
        @JsonProperty("CapturedDate") String dataCaptura,
        @JsonProperty("Status") Integer status,
        @JsonProperty("IsSplitted") Boolean valorDividido,
        @JsonProperty("ReturnMessage") String mensagemRetorno,
        @JsonProperty("ReturnCode") String codigoRetorno,
        @JsonProperty("PaymentId") String idPagamento,
        @JsonProperty("Type") String tipoPagamento,
        @JsonProperty("Currency") String moeda,
        @JsonProperty("Country") String pais,
        @JsonProperty("Links") List<CieloLinkResponse> links
) {
}