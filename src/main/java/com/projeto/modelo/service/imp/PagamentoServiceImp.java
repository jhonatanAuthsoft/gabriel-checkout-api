package com.projeto.modelo.service.imp;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.controller.dto.request.PagamentoRequestDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.BancoInterWebhookResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterBoletoPDFResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.pix.BancoInterPixResponseDTO;
import com.projeto.modelo.model.enums.MetodoPagamento;
import com.projeto.modelo.repository.ConfigWebhookRepository;
import com.projeto.modelo.service.BancoInterService;
import com.projeto.modelo.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PagamentoServiceImp implements PagamentoService {

    @Autowired
    private BancoInterService bancoInterService;

    @Autowired
    private ConfigWebhookRepository configWebhookRepository;

    @Override
    public Object pagarVenda(PagamentoRequestDTO dto) {

        BancoInterWebhookResponseDTO webhook = consultarWebhooks(dto.tipoCobranca());

        if (webhook == null) {

        }

        if (dto.tipoCobranca().equals(MetodoPagamento.PIX)) {
            return null;
        } else if (dto.tipoCobranca().equals(MetodoPagamento.BOLETO)) {
            return null;
        } else if (dto.tipoCobranca().equals(MetodoPagamento.CARTAO)) {
            return null;
        } else {
            throw new ExcecoesCustomizada("Tipo de Webhook não encontrado!", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public BancoInterWebhookResponseDTO consultarWebhooks(MetodoPagamento tipoWebhook) {
        if (tipoWebhook.equals(MetodoPagamento.PIX)) {
            return bancoInterService.buscaWebhookPix();
        } else if (tipoWebhook.equals(MetodoPagamento.BOLETO)) {
            return bancoInterService.buscaWebhookBoleto();
        } else if (tipoWebhook.equals(MetodoPagamento.CARTAO)) {
            return null;
        } else {
            throw new ExcecoesCustomizada("Tipo de Webhook não encontrado!", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public BancoInterWebhookResponseDTO cadastrarWebhooks(MetodoPagamento tipoWebhook, String baseUrl) {
        if (tipoWebhook.equals(MetodoPagamento.PIX)) {
            return null;
        } else if (tipoWebhook.equals(MetodoPagamento.BOLETO)) {
            return null;
        } else if (tipoWebhook.equals(MetodoPagamento.CARTAO)) {
            return null;
        } else {
            throw new ExcecoesCustomizada("Tipo de Webhook não encontrado!", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public BancoInterPixResponseDTO pagarComPix() {
        return null;
    }

    @Override
    public BancoInterBoletoPDFResponseDTO pagarComBoleto() {
        return null;
    }

    @Override
    public void pagarComCartao() {

    }
}
