package com.projeto.modelo.controller.dto.response;

import com.projeto.modelo.model.entity.Endereco;
import com.projeto.modelo.model.enums.UsuarioStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UsuarioResponseDTO(Long id, String email, String nome, String celular, String permissao, String cpf, UsuarioStatus status, Endereco endereco, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {

}
