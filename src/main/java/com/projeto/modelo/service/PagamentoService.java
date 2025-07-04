package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.PagamentoRequestDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.BancoInterWebhookResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterBoletoPDFResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.pix.BancoInterPixResponseDTO;
import com.projeto.modelo.model.enums.MetodoPagamento;

public interface PagamentoService {
    Object pagarVenda(PagamentoRequestDTO dto);

    BancoInterWebhookResponseDTO consultarWebhooks(MetodoPagamento tipoWebhook);

    Object cadastrarWebhooks(MetodoPagamento tipoWebhook, String baseUrl);

    BancoInterPixResponseDTO pagarComPix();

    BancoInterBoletoPDFResponseDTO pagarComBoleto();

    void pagarComCartao();


}
