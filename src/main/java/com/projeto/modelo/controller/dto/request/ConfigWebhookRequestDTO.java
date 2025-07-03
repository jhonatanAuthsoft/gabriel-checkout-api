package com.projeto.modelo.controller.dto.request;

import lombok.Builder;

@Builder
public record ConfigWebhookRequestDTO(
        String url
) {
}
