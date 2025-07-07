package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.cielo.CieloReceberPagamentoCartao;
import com.projeto.modelo.controller.dto.response.cielo.CieloResponse;
import com.projeto.modelo.controller.dto.response.cielo.CieloResponseCallback;

public interface CieloService {

    CieloResponse pagarCielo(CieloReceberPagamentoCartao dto);

    CieloResponseCallback consultarTransacaoPorPaymentId(String paymentId);
}
