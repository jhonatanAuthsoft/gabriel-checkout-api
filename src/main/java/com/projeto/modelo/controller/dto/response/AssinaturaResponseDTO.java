package com.projeto.modelo.controller.dto.response;

import com.projeto.modelo.model.entity.Plano;
import com.projeto.modelo.model.entity.Produto;
import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.model.entity.Venda;
import com.projeto.modelo.model.enums.MetodoPagamento;
import com.projeto.modelo.model.enums.StatusAssinatura;
import com.projeto.modelo.model.enums.TipoCobranca;
import lombok.Builder;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AssinaturaResponseDTO(
        Long id,
        List<Produto> produtos,
        List<Plano> planos,
        Usuario cliente,
        Venda venda,
        TipoCobranca tipoCobranca,
        MetodoPagamento metodoPagamento,
        StatusAssinatura statusAssinatura,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        LocalDateTime dataCancelamentoDatado
) {
}
