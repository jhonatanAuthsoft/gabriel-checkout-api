package com.projeto.modelo.service.imp;


import com.projeto.modelo.configuracao.JwtUtil;
import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.controller.dto.request.CadastraUsuarioDTO;
import com.projeto.modelo.controller.dto.request.UsuarioEsqueceuSenhaRequestDTO;
import com.projeto.modelo.controller.dto.request.ValidaTrocaSenhaRequestDTO;
import com.projeto.modelo.controller.dto.response.AuthenticatedResposeDTO;
import com.projeto.modelo.controller.dto.response.UsuarioResponseDTO;
import com.projeto.modelo.mapper.UsuarioMapper;
import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.model.enums.PermissaoStatus;
import com.projeto.modelo.model.enums.UsuarioStatus;
import com.projeto.modelo.repository.EmailService;
import com.projeto.modelo.repository.UsuarioRepository;
import com.projeto.modelo.service.UsuarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class UsuarioServiceImp implements UsuarioService {


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;


    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Transactional(readOnly = true)
    @Override
    public AuthenticatedResposeDTO retornoAutenticacao(String email, String jwt) {
        UsuarioResponseDTO responseDTO = this.usuarioMapper.toResponseDTO(this.buscarPorEmail(email));

        return AuthenticatedResposeDTO.builder()
                .usuarioRespose(responseDTO)
                .token(jwt)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new ExcecoesCustomizada("Usuário não encontrado", HttpStatus.NOT_FOUND));
    }

    @Override
    public void validaTrocaSenha(ValidaTrocaSenhaRequestDTO validaTrocaSenhaRequestDTO) {
        Optional<Usuario> usuarioOptional = this.usuarioRepository
                .findByEmailAndCodigoTrocaSenha(validaTrocaSenhaRequestDTO.email(), validaTrocaSenhaRequestDTO.codigo());

        if (!usuarioOptional.isPresent()) {
            throw new ExcecoesCustomizada("Codigo invalido", HttpStatus.BAD_REQUEST);
        }
        Usuario usuario = usuarioOptional.get();
        usuario.setSenha(this.passwordEncoder.encode(validaTrocaSenhaRequestDTO.senhaNova()));
        usuario.setCodigoTrocaSenha(null);
        this.usuarioRepository.save(usuario);
    }

    @Override
    public UsuarioResponseDTO cadastraUsuario(String token, CadastraUsuarioDTO cadastraUsuarioDTO) {
        Optional<Usuario> optionalUsuario = this.usuarioRepository.findByEmail(cadastraUsuarioDTO.email());
        Usuario usuarioRequisitor = null;
        if (token != null) {
            usuarioRequisitor = this.usuarioRepository.findByEmail(jwtUtil.extractUsername(token.substring(7))).orElse(null);
        }

        if (optionalUsuario.isPresent()) {
            Usuario usuarioExiste = optionalUsuario.get();

            usuarioExiste.setDataDelecao(null);
            usuarioExiste.setStatus(UsuarioStatus.ATIVO);
            this.usuarioRepository.save(usuarioExiste);
            return this.usuarioMapper.toResponseDTO(usuarioExiste);
        }

        String senha = this.gerarSenha();
        String senhaCriptografada = this.passwordEncoder.encode("123456");

        Usuario usuario = this.usuarioMapper.toEntity(cadastraUsuarioDTO, senhaCriptografada, usuarioRequisitor); //passar aqui o token, caso não venha token, cadastra como cliente independente do que ta vindo no dto

        Usuario usuarioSalvo = this.usuarioRepository.save(usuario);
        this.emailService.cadastraUsuario(cadastraUsuarioDTO.email(), senha);
        return this.usuarioMapper.toResponseDTO(usuarioSalvo);
    }

    @Override
    public UsuarioResponseDTO editarUsuario(String token, CadastraUsuarioDTO dto, Long idUsuario) {
        Usuario usuarioSolicitante = this.usuarioRepository.findByEmail(jwtUtil.extractUsername(token.substring(7))).orElseThrow(() -> new ExcecoesCustomizada("Usuário não encontrado!", HttpStatus.NOT_FOUND));
        Usuario usuarioAEditar = this.usuarioRepository.findById(idUsuario).orElseThrow(() -> new ExcecoesCustomizada("Usuário não encontrado!", HttpStatus.NOT_FOUND));

        if (!usuarioSolicitante.getId().equals(idUsuario) && usuarioAEditar.getPermissao().equals(PermissaoStatus.ADMIN)) {
            throw new ExcecoesCustomizada("Você não pode editar outros administradores", HttpStatus.UNAUTHORIZED);
        }

        if (usuarioAEditar.getPermissao().equals(PermissaoStatus.CLIENTE)) {
            if (!usuarioRepository.findByEmailOrCpfAndNotId(dto.email(), dto.cpf(), usuarioAEditar.getId()).isEmpty())
                throw new ExcecoesCustomizada("CPF ou Email já existem na base de dados!", HttpStatus.BAD_REQUEST);
        } else {
            if (!usuarioRepository.findByEmailAndNotId(dto.email(), usuarioAEditar.getId()).isEmpty())
                throw new ExcecoesCustomizada("Email já existem na base de dados!", HttpStatus.BAD_REQUEST);
        }

        usuarioMapper.editarUsuario(usuarioAEditar, dto);
        return usuarioMapper.toResponseDTO(usuarioRepository.save(usuarioAEditar));
    }

    @Override
    public void deletarUsuario(String token, Long id) {
        Usuario usuarioSolicitante = this.usuarioRepository.findByEmail(jwtUtil.extractUsername(token.substring(7))).orElseThrow(() -> new ExcecoesCustomizada("Usuário não encontrado!", HttpStatus.NOT_FOUND));
        Usuario usuarioASerDeletado = this.usuarioRepository.findById(id).orElseThrow(() -> new ExcecoesCustomizada("Usuário não encontrado!", HttpStatus.NOT_FOUND));

        if (usuarioSolicitante.getId().equals(usuarioASerDeletado.getId()) || usuarioASerDeletado.getPermissao().equals(PermissaoStatus.ADMIN)) {
            throw new ExcecoesCustomizada("Você não pode deletar a sí mesmo ou outros administradores!", HttpStatus.BAD_REQUEST);
        }

        usuarioASerDeletado.setStatus(UsuarioStatus.INATIVO);
        usuarioASerDeletado.setDataDelecao(LocalDateTime.now());
    }

    @Override
    public Page<UsuarioResponseDTO> listarUsuarios(int size, int page) {
        return usuarioMapper.UsuarioToResponseList(usuarioRepository.listarUsuarios(PageRequest.of(page, size)));
    }

    @Override
    public Page<UsuarioResponseDTO> listarClientes(int size, int page) {
        return usuarioMapper.UsuarioToResponseList(usuarioRepository.listarTodosClientes(PageRequest.of(page, size)));
    }

    public String gerarSenha() {
        SecureRandom random = new SecureRandom();
        StringBuilder senha = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            senha.append(random.nextInt(10)); // Gera um número entre 0 e 9
        }

        return senha.toString();
    }

    @Override
    public void esqueceuSenha(UsuarioEsqueceuSenhaRequestDTO usuarioEsqueceuSenhaRequestDTO) {
        try {
            Optional<Usuario> usuarioOptional = this.usuarioRepository.findByEmailAndStatus(usuarioEsqueceuSenhaRequestDTO.email(), UsuarioStatus.ATIVO);
            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                Random random = new Random();
                int codigo = 1000 + random.nextInt(9000);
                usuario.setCodigoTrocaSenha(codigo);
                this.usuarioRepository.save(usuario);
                this.emailService.enviarEmailEsqueceuSenha(usuarioEsqueceuSenhaRequestDTO.email());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
