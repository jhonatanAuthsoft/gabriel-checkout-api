package com.projeto.modelo.controller.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record RelatorioVendasDTO(
        List<Long> ids
) {
}
