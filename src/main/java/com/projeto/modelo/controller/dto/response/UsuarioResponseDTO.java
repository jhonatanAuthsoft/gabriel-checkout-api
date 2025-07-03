package com.projeto.modelo.controller.dto.response;

import com.projeto.modelo.model.entity.Endereco;
import lombok.Builder;

@Builder
public record UsuarioResponseDTO(Long id, String email, String nome, String permissao, String cpf, Endereco endereco) {

}
