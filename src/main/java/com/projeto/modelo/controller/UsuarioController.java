package com.projeto.modelo.controller;



import com.projeto.modelo.controller.dto.request.CadastraUsuarioDTO;
import com.projeto.modelo.controller.dto.request.UsuarioEsqueceuSenhaRequestDTO;
import com.projeto.modelo.controller.dto.request.ValidaTrocaSenhaRequestDTO;
import com.projeto.modelo.controller.dto.response.UsuarioResposeDTO;
import com.projeto.modelo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;


    @PostMapping(value = "/validar-troca-senha")
    public ResponseEntity<UsuarioResposeDTO> validarTrocaSenha(@RequestBody ValidaTrocaSenhaRequestDTO usuarioEsqueceuSenhaRequestDTO) {
        this.usuarioService.validaTrocaSenha(usuarioEsqueceuSenhaRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

  //  @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/cadastrar")
    public ResponseEntity<UsuarioResposeDTO> cadastrar(@RequestBody CadastraUsuarioDTO cadastraUsuarioDTO) {
        UsuarioResposeDTO usuarioResposeDTO = this.usuarioService.cadastraUsuario(cadastraUsuarioDTO);
        return new ResponseEntity<>(usuarioResposeDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/esqueceu-senha")
    public ResponseEntity<UsuarioResposeDTO> esqueceuSenha(@RequestBody UsuarioEsqueceuSenhaRequestDTO usuarioEsqueceuSenhaRequestDTO) {
        this.usuarioService.esqueceuSenha(usuarioEsqueceuSenhaRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
