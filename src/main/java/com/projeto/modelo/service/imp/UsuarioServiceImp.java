package com.projeto.modelo.service.imp;



import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.controller.dto.request.CadastraUsuarioDTO;
import com.projeto.modelo.controller.dto.request.UsuarioEsqueceuSenhaRequestDTO;
import com.projeto.modelo.controller.dto.request.ValidaTrocaSenhaRequestDTO;
import com.projeto.modelo.controller.dto.response.AuthenticatedResposeDTO;
import com.projeto.modelo.controller.dto.response.UsuarioResposeDTO;
import com.projeto.modelo.mapper.UsuarioMapper;
import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.model.enums.PermissaoStatus;
import com.projeto.modelo.model.enums.UsuarioStatus;
import com.projeto.modelo.repository.EmailService;
import com.projeto.modelo.repository.UsuarioRepository;
import com.projeto.modelo.service.UsuarioService;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImp implements UsuarioService {


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private EmailService emailService;



    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Transactional(readOnly = true)
    @Override
    public AuthenticatedResposeDTO retornoAutenticacao(String email, String jwt){
        UsuarioResposeDTO responseDTO = this.usuarioMapper.toResponseDTO(this.buscarPorEmail(email));

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
    public void validaTrocaSenha(ValidaTrocaSenhaRequestDTO validaTrocaSenhaRequestDTO){
        Optional<Usuario> usuarioOptional = this.usuarioRepository
                .findByEmailAndCodigoTrocaSenha(validaTrocaSenhaRequestDTO.email(), validaTrocaSenhaRequestDTO.codigo());

        if(!usuarioOptional.isPresent()){
            throw new ExcecoesCustomizada("Codigo invalido",HttpStatus.BAD_REQUEST);
        }
        Usuario usuario = usuarioOptional.get();
        usuario.setSenha(this.passwordEncoder.encode(validaTrocaSenhaRequestDTO.senhaNova()));
        usuario.setCodigoTrocaSenha(null);
        this.usuarioRepository.save(usuario);
    }

    @Override
    public UsuarioResposeDTO cadastraUsuario(CadastraUsuarioDTO cadastraUsuarioDTO){

        Usuario usuario = new Usuario();
        String senha = this.gerarSenha();
        String senhaCriptografada = this.passwordEncoder.encode(senha);

        usuario.setEmail(cadastraUsuarioDTO.email());
        usuario.setStatus(UsuarioStatus.ATIVO);
        usuario.setSenha(senhaCriptografada);
        usuario.setPermissao(PermissaoStatus.ADMIN);
        Usuario usuarioSalvo = this.usuarioRepository.save(usuario);
        this.emailService.cadastraUsuario(cadastraUsuarioDTO.email(), senha);
        UsuarioResposeDTO responseDTO = this.usuarioMapper.toResponseDTO(usuarioSalvo);
        return responseDTO;
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
