package com.projeto.modelo.controller.dto.request.cielo;

public record CieloCadastrarWebhookRequest(
        String url,
        String[] eventos
) {
}
