package com.projeto.modelo.controller.dto.request.bancoInter.pix;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.util.StringUtils;
import java.math.BigDecimal;
import java.util.Objects;
import org.springframework.http.HttpStatus;

public record BancoInterPixRequestDTO(Integer expiracao, String devedorNome, String devedorCpfCnpj, BigDecimal valorPagamento) {


    public BancoInterPixRequestDTO {

        if(!StringUtils.isNullOrEmpty(devedorNome)){
            if(devedorNome.length() > 200){
                throw new ExcecoesCustomizada("Nome não pode ter mais de 200 caracteres.", HttpStatus.BAD_GATEWAY);
            }

            if(StringUtils.isNullOrEmpty(devedorCpfCnpj) && !StringUtils.cpfOuCnpjValido(devedorCpfCnpj)){
                throw new ExcecoesCustomizada("Necessário informar CPF ou CNPJ do devedor.", HttpStatus.BAD_GATEWAY);
            }
        }

        if(!StringUtils.isNullOrEmpty(devedorCpfCnpj) && !StringUtils.cpfOuCnpjValido(devedorCpfCnpj)){
            if(StringUtils.isNullOrEmpty(devedorNome)){
                throw new ExcecoesCustomizada("Necessário informar o nome do devedor.", HttpStatus.BAD_GATEWAY);
            }
        }

        if(Objects.isNull(valorPagamento) || valorPagamento.compareTo(BigDecimal.ZERO) == 0){
            throw new ExcecoesCustomizada("O valor informado está incorreto.", HttpStatus.BAD_GATEWAY);
        }

    }
}
