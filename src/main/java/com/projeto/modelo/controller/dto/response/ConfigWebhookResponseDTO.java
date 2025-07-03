package com.projeto.modelo.controller.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ConfigWebhookResponseDTO(
        Long id,
        String url,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
