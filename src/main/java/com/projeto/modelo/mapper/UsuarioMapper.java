package com.projeto.modelo.mapper;


import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.controller.dto.request.CadastraUsuarioDTO;
import com.projeto.modelo.controller.dto.response.UsuarioResponseDTO;
import com.projeto.modelo.model.entity.Endereco;
import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.model.enums.PermissaoStatus;
import com.projeto.modelo.model.enums.UsuarioStatus;
import com.projeto.modelo.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .email(usuario.getEmail())
                .celular(usuario.getCelular())
                .nome(usuario.getNome())
                .cpf(usuario.getCpf())
                .endereco(usuario.getEndereco())
                .permissao(usuario.getPermissao().toString())
                .build();
    }

    public Usuario toEntity(CadastraUsuarioDTO dto, String senhaGerada, Usuario usuario) {
        if (usuario != null && usuario.getPermissao() == PermissaoStatus.ADMIN) {
            return Usuario.builder()
                    .nome(dto.nome())
                    .email(dto.email())
                    .celular(dto.celular())
                    .cpf(dto.cpf())
                    .endereco(dto.endereco())
                    .status(UsuarioStatus.ATIVO)
                    .permissao(dto.permissao())
                    .senha(senhaGerada)
                    .build();
        } else {
            if (StringUtils.isNullOrEmpty(dto.cpf()) ||
                StringUtils.isNullOrEmpty(dto.email()) ||
                StringUtils.isNullOrEmpty(dto.celular()) ||
                this.validarEndereco(dto.endereco())) {
                throw new ExcecoesCustomizada("Existem dados obrigatórios faltantes!", HttpStatus.BAD_REQUEST);
            }

            return Usuario.builder()
                    .nome(dto.nome())
                    .email(dto.email())
                    .celular(dto.celular())
                    .cpf(dto.cpf())
                    .endereco(dto.endereco())
                    .status(UsuarioStatus.ATIVO)
                    .permissao(PermissaoStatus.CLIENTE)
                    .senha(senhaGerada)
                    .build();
        }
    }

    private Boolean validarEndereco(Endereco endereco) {
        if (endereco == null) {
            return true;
        }

        return StringUtils.isNullOrEmpty(endereco.endereco()) ||
               StringUtils.isNullOrEmpty(endereco.numeroResidencia()) ||
               StringUtils.isNullOrEmpty(endereco.complementoEndereco()) ||
               StringUtils.isNullOrEmpty(endereco.bairro()) ||
               StringUtils.isNullOrEmpty(endereco.cidade()) ||
               endereco.uf() == null ||
               StringUtils.isNullOrEmpty(endereco.cep());
    }

    public Page<UsuarioResponseDTO> UsuarioToResponseList(Page<Usuario> usuarios) {
        return usuarios.map(this::toResponseDTO);
    }

    public void editarUsuario(Usuario usuario, CadastraUsuarioDTO dto) {
        PermissaoStatus permissao = usuario.getPermissao().equals(PermissaoStatus.ADMIN) ? dto.permissao() : usuario.getPermissao();

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setCpf(dto.cpf());
        usuario.setEndereco(dto.endereco());
        usuario.setPermissao(permissao);
    }
}
