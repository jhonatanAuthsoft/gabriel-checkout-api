package com.projeto.modelo.controller;


import com.projeto.modelo.controller.dto.request.CadastraUsuarioDTO;
import com.projeto.modelo.controller.dto.request.UsuarioEsqueceuSenhaRequestDTO;
import com.projeto.modelo.controller.dto.request.ValidaTrocaSenhaRequestDTO;
import com.projeto.modelo.controller.dto.response.UsuarioResponseDTO;
import com.projeto.modelo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;


    @PostMapping(value = "/validar-troca-senha")
    public ResponseEntity<UsuarioResponseDTO> validarTrocaSenha(@RequestBody ValidaTrocaSenhaRequestDTO usuarioEsqueceuSenhaRequestDTO) {
        this.usuarioService.validaTrocaSenha(usuarioEsqueceuSenhaRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/cadastrar")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestHeader(value = "Authorization", required = false) String token,
                                                        @RequestBody CadastraUsuarioDTO cadastraUsuarioDTO) {
        UsuarioResponseDTO usuarioResponseDTO = this.usuarioService.cadastraUsuario(token, cadastraUsuarioDTO);
        return new ResponseEntity<>(usuarioResponseDTO, HttpStatus.OK);
    }

    @PostMapping(value = "/esqueceu-senha")
    public ResponseEntity<UsuarioResponseDTO> esqueceuSenha(@RequestBody UsuarioEsqueceuSenhaRequestDTO usuarioEsqueceuSenhaRequestDTO) {
        this.usuarioService.esqueceuSenha(usuarioEsqueceuSenhaRequestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/atualizar/{idUsuario}")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(@RequestHeader(value = "Authorization") String token,
                                                               @RequestBody CadastraUsuarioDTO dto, @PathVariable Long idUsuario) {
        return new ResponseEntity<>(usuarioService.editarUsuario(token, dto, idUsuario), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deletar/{idUsuario}")
    public ResponseEntity<Void> atualizarUsuario(@RequestHeader(value = "Authorization") String token,
                                                 @PathVariable Long idUsuario) {
        usuarioService.deletarUsuario(token, idUsuario);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @GetMapping("/listar-todos/clientes")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarTodosClientes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(usuarioService.listarClientes(size, page), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listar-todos/usuarios")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarTodosUsuarios(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(usuarioService.listarUsuarios(size, page), HttpStatus.OK);
    }
}
