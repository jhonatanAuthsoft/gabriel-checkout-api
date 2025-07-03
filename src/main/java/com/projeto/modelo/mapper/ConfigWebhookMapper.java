package com.projeto.modelo.mapper;

import com.projeto.modelo.controller.dto.request.ConfigWebhookRequestDTO;
import com.projeto.modelo.controller.dto.response.ConfigWebhookResponseDTO;
import com.projeto.modelo.model.entity.ConfigWebhook;
import org.springframework.stereotype.Component;

@Component
public class ConfigWebhookMapper {

    public ConfigWebhookResponseDTO toResponseDTO(ConfigWebhook configWebhook) {
        return ConfigWebhookResponseDTO.builder()
                .id(configWebhook.getId())
                .url(configWebhook.getUrl())
                .dataCriacao(configWebhook.getDataCriacao())
                .dataAtualizacao(configWebhook.getDataAtualizacao())
                .build();
    }

    public ConfigWebhook toEntity(ConfigWebhookRequestDTO dto) {
        return ConfigWebhook.builder()
                .url(dto.url())
                .build();
    }

    public void atualizarWebhook(ConfigWebhookRequestDTO dto, ConfigWebhook configWebhook) {
        configWebhook.setUrl(dto.url());
    }
}
