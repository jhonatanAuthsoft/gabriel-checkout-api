package com.projeto.modelo.controller.dto.request.bancoInter.pix;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.util.StringUtils;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record BancoInterWebhookRequestDTO(String webhookUrl) {


    public BancoInterWebhookRequestDTO {

        if (StringUtils.isNullOrEmpty(webhookUrl)) {
            throw new ExcecoesCustomizada("Informe a URL do Webhook.", HttpStatus.BAD_GATEWAY);
        }

    }
}
