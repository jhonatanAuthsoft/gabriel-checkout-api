package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.CadastraUsuarioDTO;
import com.projeto.modelo.controller.dto.request.UsuarioEsqueceuSenhaRequestDTO;
import com.projeto.modelo.controller.dto.request.ValidaTrocaSenhaRequestDTO;
import com.projeto.modelo.controller.dto.response.AuthenticatedResposeDTO;
import com.projeto.modelo.controller.dto.response.UsuarioResponseDTO;
import com.projeto.modelo.model.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface UsuarioService {

    @Transactional(readOnly = true)
    AuthenticatedResposeDTO retornoAutenticacao(String email, String jwt);

    @Transactional(readOnly = true)
    Usuario buscarPorEmail(String email);

    void validaTrocaSenha(ValidaTrocaSenhaRequestDTO validaTrocaSenhaRequestDTO);

    UsuarioResponseDTO cadastraUsuario(String token, CadastraUsuarioDTO cadastraUsuarioDTO);

    UsuarioResponseDTO editarUsuario(String token, CadastraUsuarioDTO dto, Long idUsuario);

    void deletarUsuario(String token, Long id);

    Page<UsuarioResponseDTO> listarUsuarios(int size, int page);

    Page<UsuarioResponseDTO> listarClientes(int size, int page);

    void esqueceuSenha(UsuarioEsqueceuSenhaRequestDTO usuarioEsqueceuSenhaRequestDTO);
}
