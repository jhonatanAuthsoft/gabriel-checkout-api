package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.CadastraUsuarioDTO;
import com.projeto.modelo.controller.dto.request.UsuarioEsqueceuSenhaRequestDTO;
import com.projeto.modelo.controller.dto.request.ValidaTrocaSenhaRequestDTO;
import com.projeto.modelo.controller.dto.response.AuthenticatedResposeDTO;
import com.projeto.modelo.controller.dto.response.UsuarioResposeDTO;
import com.projeto.modelo.model.entity.Usuario;
import org.springframework.transaction.annotation.Transactional;

public interface UsuarioService {

    @Transactional(readOnly = true)
    AuthenticatedResposeDTO retornoAutenticacao(String email, String jwt);

    @Transactional(readOnly = true)
    Usuario buscarPorEmail(String email);

    void validaTrocaSenha(ValidaTrocaSenhaRequestDTO validaTrocaSenhaRequestDTO);

    UsuarioResposeDTO cadastraUsuario(CadastraUsuarioDTO cadastraUsuarioDTO);

    void esqueceuSenha(UsuarioEsqueceuSenhaRequestDTO usuarioEsqueceuSenhaRequestDTO);
}
