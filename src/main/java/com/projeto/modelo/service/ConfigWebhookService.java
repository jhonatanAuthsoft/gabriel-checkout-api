package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.ConfigWebhookRequestDTO;
import com.projeto.modelo.controller.dto.response.ConfigWebhookResponseDTO;

public interface ConfigWebhookService {

    ConfigWebhookResponseDTO criarWebhook(ConfigWebhookRequestDTO dto);

    ConfigWebhookResponseDTO atualizarWebhook(ConfigWebhookRequestDTO dto, Long idWebhook);

    ConfigWebhookResponseDTO listarWebhook();
}
