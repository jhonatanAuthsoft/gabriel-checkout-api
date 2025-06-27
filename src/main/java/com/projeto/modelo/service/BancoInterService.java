package com.projeto.modelo.service;


import com.projeto.modelo.controller.dto.request.bancoInter.boleto.BancoInterBoletoRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.boleto.BancoInterCallbackBoletoDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.pix.BancoInterPixRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.pix.BancoInterWebhookRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.pix.calback.BancoInterCallbackPixDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.BancoInterWebhookResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterBoletoPDFResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterBoletoResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterCodigoBoletoResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.pix.BancoInterPixResponseDTO;
import java.util.List;

public interface BancoInterService {
    BancoInterPixResponseDTO geraPix(BancoInterPixRequestDTO bancoInterPixRequestDTO);

    BancoInterPixResponseDTO consultaPix(String txid);

    void cadastraWebhookPix(BancoInterWebhookRequestDTO webhookPixRequest);

    BancoInterWebhookResponseDTO buscaWebhookPix();

    void deletarWebhookPix();

    void callbackPix(List<BancoInterCallbackPixDTO> bancoInterCallbackPixDTO);

    BancoInterCodigoBoletoResponseDTO gerarBoleto(BancoInterBoletoRequestDTO bancoInterBoletoRequestDTO);

    BancoInterBoletoResponseDTO consultaBoleto(String codigoBoleto);

    BancoInterBoletoPDFResponseDTO consultarBoletoPdf(String codigoBoleto);

    void cadastraWebhookBoleto(BancoInterWebhookRequestDTO webhookPixRequest);

    BancoInterWebhookResponseDTO buscaWebhookBoleto();

    void deletarWebhookBoleto();

    void callbackBoleto(List<BancoInterCallbackBoletoDTO> bancoInterCallbackBoletoDTO);
}
