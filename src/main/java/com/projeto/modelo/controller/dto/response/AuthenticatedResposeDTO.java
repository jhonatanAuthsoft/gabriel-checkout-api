package com.projeto.modelo.controller.dto.response;

import lombok.Builder;

@Builder
public record AuthenticatedResposeDTO(UsuarioResponseDTO usuarioRespose, String token) {

}
