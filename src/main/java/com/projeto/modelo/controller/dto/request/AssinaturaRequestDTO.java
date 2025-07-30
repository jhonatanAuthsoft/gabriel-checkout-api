package com.projeto.modelo.controller.dto.request;

import com.projeto.modelo.model.enums.MetodoPagamento;
import com.projeto.modelo.model.enums.StatusAssinatura;
import com.projeto.modelo.model.enums.TipoCobranca;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AssinaturaRequestDTO(
        List<Long> idsProduto,
        List<Long> idsPlano,
        Long idUsuario,
        Long idVenda,
        TipoCobranca tipoCobranca,
        MetodoPagamento metodoPagamento,
        StatusAssinatura statusAssinatura,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        LocalDateTime dataCancelamentoDatado
) {
}
