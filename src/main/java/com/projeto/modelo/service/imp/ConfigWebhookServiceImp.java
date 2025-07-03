package com.projeto.modelo.service.imp;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.controller.dto.request.ConfigWebhookRequestDTO;
import com.projeto.modelo.controller.dto.response.ConfigWebhookResponseDTO;
import com.projeto.modelo.mapper.ConfigWebhookMapper;
import com.projeto.modelo.model.entity.ConfigWebhook;
import com.projeto.modelo.repository.ConfigWebhookRepository;
import com.projeto.modelo.service.ConfigWebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConfigWebhookServiceImp implements ConfigWebhookService {

    @Autowired
    private ConfigWebhookRepository configWebhookRepository;

    @Autowired
    private ConfigWebhookMapper configWebhookMapper;

    @Override
    public ConfigWebhookResponseDTO criarWebhook(ConfigWebhookRequestDTO dto) {
        ConfigWebhookResponseDTO webhookExists = this.listarWebhook();

        if (webhookExists != null) {
            return this.atualizarWebhook(dto, webhookExists.id());
        }
        ConfigWebhook webhookCriado = configWebhookMapper.toEntity(dto);
        configWebhookRepository.save(webhookCriado);
        return configWebhookMapper.toResponseDTO(webhookCriado);
    }

    @Override
    public ConfigWebhookResponseDTO atualizarWebhook(ConfigWebhookRequestDTO dto, Long idWebhook) {
        Optional<ConfigWebhook> configWebhook = configWebhookRepository.findById(idWebhook);

        if (configWebhook.isPresent()) {
            configWebhookMapper.atualizarWebhook(dto, configWebhook.get());
            configWebhookRepository.save(configWebhook.get());
            return configWebhookMapper.toResponseDTO(configWebhook.get());
        } else {
            throw new ExcecoesCustomizada("Webhook não encontrado!", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ConfigWebhookResponseDTO listarWebhook() {
        List<ConfigWebhook> webhooks = configWebhookRepository.findAll();

        if (webhooks.isEmpty()) {
            return null;
        } else {
            return configWebhookMapper.toResponseDTO(webhooks.get(0));
        }
    }
}
