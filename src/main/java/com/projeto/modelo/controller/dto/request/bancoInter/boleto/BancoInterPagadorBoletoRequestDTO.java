package com.projeto.modelo.controller.dto.request.bancoInter.boleto;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.model.enums.Uf;
import com.projeto.modelo.util.StringUtils;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Builder
public record BancoInterPagadorBoletoRequestDTO(String email, String ddd, String telefone, String numeroResidencia,
                                                String complementoEndereco, String cpfCnpj, String nome,
                                                String endereco, String bairro, String cidade, Uf uf, String cep) {


    public BancoInterPagadorBoletoRequestDTO {

        if (!StringUtils.isNullOrEmpty(email)) {
            if (email.length() > 50) {
                throw new ExcecoesCustomizada("E-mail muito longo, favor informe um e-mail mais curto.", HttpStatus.BAD_GATEWAY);
            }
        }

        if (!StringUtils.isNullOrEmpty(ddd)) {
            if (ddd.length() > 2) {
                throw new ExcecoesCustomizada("DDD deve conter no maximo 2 dígitos", HttpStatus.BAD_GATEWAY);
            }
        }

        if (!StringUtils.isNullOrEmpty(telefone)) {
            if (telefone.length() > 9) {
                throw new ExcecoesCustomizada("Telefone pode ter no maximo 9 dígitos ", HttpStatus.BAD_GATEWAY);
            }
        }

        if (!StringUtils.isNullOrEmpty(numeroResidencia)) {
            if (numeroResidencia.length() > 10) {
                throw new ExcecoesCustomizada("Numero da residencia pode ter no maximo 10 dígitos ", HttpStatus.BAD_GATEWAY);
            }
        }

        if (!StringUtils.isNullOrEmpty(complementoEndereco)) {
            if (complementoEndereco.length() > 30) {
                throw new ExcecoesCustomizada("Complemento da residencia pode ter no maximo 30 dígitos ", HttpStatus.BAD_GATEWAY);
            }
        }

        if (StringUtils.isNullOrEmpty(cpfCnpj)) {
            throw new ExcecoesCustomizada("Deve informar o CPF ou CNPJ do pagador ", HttpStatus.BAD_GATEWAY);
        } else {
            if (!StringUtils.cpfOuCnpjValido(cpfCnpj)) {
                throw new ExcecoesCustomizada("CPF ou CNPJ inválido", HttpStatus.BAD_GATEWAY);
            }
        }

        if (StringUtils.isNullOrEmpty(nome)) {
            throw new ExcecoesCustomizada("Deve informar o nome do pagador ", HttpStatus.BAD_GATEWAY);
        } else {
            if (nome.length() > 100) {
                throw new ExcecoesCustomizada("Nome do pagador pode ter no maximo 100 dígitos ", HttpStatus.BAD_GATEWAY);
            }
        }

        if (StringUtils.isNullOrEmpty(endereco)) {
            throw new ExcecoesCustomizada("Deve informar o endereço do pagador ", HttpStatus.BAD_GATEWAY);
        } else {
            if (endereco.length() > 100) {
                throw new ExcecoesCustomizada("Endereço do pagador pode ter no maximo 100 dígitos ", HttpStatus.BAD_GATEWAY);
            }
        }

        if (!StringUtils.isNullOrEmpty(bairro)) {
            if (bairro.length() > 100) {
                throw new ExcecoesCustomizada("Bairro do pagador pode ter no maximo 60 dígitos ", HttpStatus.BAD_GATEWAY);
            }
        }

        if (StringUtils.isNullOrEmpty(cidade)) {
            throw new ExcecoesCustomizada("Deve informar a cidade do pagador ", HttpStatus.BAD_GATEWAY);
        } else {
            if (cidade.length() > 100) {
                throw new ExcecoesCustomizada("Cidade do pagador pode ter no maximo 100 dígitos ", HttpStatus.BAD_GATEWAY);
            }
        }

        if (Objects.isNull(uf)) {
            throw new ExcecoesCustomizada("Deve informar a UF do pagador ", HttpStatus.BAD_GATEWAY);
        }

        if (StringUtils.isNullOrEmpty(cep)) {
            throw new ExcecoesCustomizada("Deve informar a CEP do pagador ", HttpStatus.BAD_GATEWAY);
        } else {
            if (cidade.length() == 8) {
                throw new ExcecoesCustomizada("CEP inválido", HttpStatus.BAD_GATEWAY);
            }
        }

    }
}
