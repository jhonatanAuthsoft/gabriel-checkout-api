package com.projeto.modelo.controller.dto.response.dashboard;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Reembolsos(
        Long idVenda,
        String nome,
        LocalDateTime dataReembolso
) {
}
