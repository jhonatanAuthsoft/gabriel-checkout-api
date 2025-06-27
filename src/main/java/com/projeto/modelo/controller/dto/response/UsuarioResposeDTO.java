package com.projeto.modelo.controller.dto.response;

import lombok.Builder;

@Builder
public record UsuarioResposeDTO(Long id, String email, String permissao) {

}
