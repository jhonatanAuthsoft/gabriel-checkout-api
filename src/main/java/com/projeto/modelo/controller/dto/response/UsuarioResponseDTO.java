package com.projeto.modelo.controller.dto.response;

import lombok.Builder;

@Builder
public record UsuarioResponseDTO(Long id, String email, String nome, String permissao) {

}
