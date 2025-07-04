package com.projeto.modelo.controller.dto.request.bancoInter.boleto;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.util.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record BancoInterBoletoRequestDTO(BigDecimal valorPagamento, LocalDate dataVencimento, BancoInterPagadorBoletoRequestDTO pagador) {


    public BancoInterBoletoRequestDTO {

        if(Objects.isNull(valorPagamento) || valorPagamento.compareTo(BigDecimal.ZERO) == 0){
            throw new ExcecoesCustomizada("O valor informado está incorreto.", HttpStatus.BAD_GATEWAY);
        }

        if(Objects.isNull(dataVencimento) || dataVencimento.isBefore(LocalDate.now())){
            throw new ExcecoesCustomizada("Data de vencimento não pode ser menor que a data de hoje.", HttpStatus.BAD_GATEWAY);
        }

    }
}
