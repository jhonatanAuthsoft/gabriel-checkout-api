package com.projeto.modelo.mapper;



import com.projeto.modelo.controller.dto.response.UsuarioResposeDTO;
import com.projeto.modelo.model.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {



    public UsuarioResposeDTO toResponseDTO(Usuario usuario) {
        return UsuarioResposeDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .permissao(usuario.getPermissao().toString())
                .build();
    }

}
