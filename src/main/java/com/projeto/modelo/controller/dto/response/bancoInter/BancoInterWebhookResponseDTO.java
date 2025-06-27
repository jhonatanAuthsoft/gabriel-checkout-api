package com.projeto.modelo.controller.dto.response.bancoInter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BancoInterWebhookResponseDTO {
    private String webhookUrl;
    private String chave;
    private String criacao;
}
