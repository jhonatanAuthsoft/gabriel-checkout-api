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
                .url(formatUrl(dto.url()))
                .build();
    }

    public void atualizarWebhook(ConfigWebhookRequestDTO dto, ConfigWebhook configWebhook) {
        configWebhook.setUrl(formatUrl(dto.url()));
    }

    private String formatUrl(String url) {
        url = url.replaceAll("/+$", "");
        return url + "/";
    }
}
