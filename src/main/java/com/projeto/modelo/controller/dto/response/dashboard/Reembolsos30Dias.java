package com.projeto.modelo.controller.dto.response.dashboard;

import lombok.Builder;

import java.util.List;

@Builder
public record Reembolsos30Dias(
        Integer totalReembolsos,
        List<Reembolsos> reembolsos
) {


}
