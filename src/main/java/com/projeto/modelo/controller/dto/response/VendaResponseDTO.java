package com.projeto.modelo.controller.dto.response;

import com.projeto.modelo.model.entity.Cupom;
import com.projeto.modelo.model.entity.Plano;
import com.projeto.modelo.model.entity.Produto;
import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.model.enums.*;
import lombok.Builder;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record VendaResponseDTO(
        Long id,
        List<Produto> produtos,
        BigDecimal valorPago,
        String txid,
        String codigoSolicitacao,
        Cupom cupomUsado,
        List<Plano> planos,
        OrigemCompra origemCompra,
        MetodoPagamento metodoPagamento,
        StatusPagamento statusPagamento,
        StatusVenda statusVenda,
        TipoCobranca tipoRecorrencia,
        Usuario cliente,
        Usuario vendedor,
        LocalDateTime dataCompra,
        LocalDateTime dataPagamento,
        LocalDateTime dataAtualizacao,
        LocalDateTime dataReembolso
) {
}
