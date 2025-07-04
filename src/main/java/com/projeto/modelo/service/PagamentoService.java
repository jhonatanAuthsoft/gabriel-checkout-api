package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.PagamentoRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.boleto.BancoInterCallbackBoletoDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.pix.calback.BancoInterCallbackPixDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.BancoInterWebhookResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterBoletoPDFResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.pix.BancoInterPixResponseDTO;
import com.projeto.modelo.model.enums.MetodoPagamento;

import java.util.List;

public interface PagamentoService {
    BancoInterWebhookResponseDTO consultarWebhooks(MetodoPagamento tipoWebhook);

    void cadastrarWebhooks(MetodoPagamento tipoWebhook, String baseUrl);

    BancoInterPixResponseDTO pagarComPix(PagamentoRequestDTO dto);

    BancoInterBoletoPDFResponseDTO pagarComBoleto(PagamentoRequestDTO dto);

    void pagarComCartao();

    void callbackPix(List<BancoInterCallbackPixDTO> bancoInterCallbackPixDTO) ;

    void callbackBoleto(List<BancoInterCallbackBoletoDTO> bancoInterCallbackBoleto);

    void callbackCartao();



}
