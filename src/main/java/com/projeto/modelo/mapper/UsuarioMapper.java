package com.projeto.modelo.mapper;


import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.controller.dto.request.CadastraUsuarioDTO;
import com.projeto.modelo.controller.dto.response.UsuarioResponseDTO;
import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.model.enums.PermissaoStatus;
import com.projeto.modelo.model.enums.UsuarioStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .permissao(usuario.getPermissao().toString())
                .build();
    }

    public Usuario toEntity(CadastraUsuarioDTO dto, String senhaGerada) {

        PermissaoStatus permissao = dto.permissao() != null ? dto.permissao() : PermissaoStatus.CLIENTE;

        if (permissao.equals(PermissaoStatus.CLIENTE)) {
            if (dto.cpf() == null || dto.celular() == null) {
                throw new ExcecoesCustomizada("O CPF e/ou Celular são obrigatórios ao cadastrar um novo CLIENTE!", HttpStatus.BAD_REQUEST);
            }
        }
        return Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .celular(dto.celular())
                .cpf(dto.cpf())
                .status(UsuarioStatus.ATIVO)
                .permissao(permissao)
                .senha(senhaGerada)
                .build();
    }
}
