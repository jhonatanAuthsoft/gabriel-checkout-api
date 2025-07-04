package com.projeto.modelo.controller.dto.response;

import com.projeto.modelo.model.entity.Endereco;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UsuarioResponseDTO(Long id, String email, String nome, String celular, String permissao, String cpf, Endereco endereco, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {

}
