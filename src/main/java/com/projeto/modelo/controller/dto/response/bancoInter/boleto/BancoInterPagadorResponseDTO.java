package com.projeto.modelo.controller.dto.response.bancoInter.boleto;

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
public class BancoInterPagadorResponseDTO {
    private String email;
    private String ddd;
    private String telefone;
    private String numero;
    private String complemento;
    private String cpfCnpj;
    private String tipoPessoa;
    private String nome;
    private String endereco;
    private String bairro;
    private String cidade;
    private String uf;
    private String cep;
}
