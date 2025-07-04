package com.projeto.modelo.controller;

import com.projeto.modelo.controller.dto.request.AssinaturaRequestDTO;
import com.projeto.modelo.controller.dto.response.AssinaturaResponseDTO;
import com.projeto.modelo.service.AssinaturaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assinatura")
public class AssinaturaController {

    @Autowired
    private AssinaturaService assinaturaService;

    @PostMapping("/criar")
    public ResponseEntity<AssinaturaResponseDTO> criarAssinatura(@RequestBody @Valid AssinaturaRequestDTO dto) {
        return new ResponseEntity<>(assinaturaService.criarAssinatura(dto), HttpStatus.CREATED);
    }

    @PutMapping("/atualizar/{idAssinatura}")
    public ResponseEntity<AssinaturaResponseDTO> atualizarAssinatura(
            @PathVariable Long idAssinatura,
            @RequestBody @Valid AssinaturaRequestDTO dto
    ) {
        return new ResponseEntity<>(assinaturaService.atualizarAssinatura(idAssinatura, dto), HttpStatus.OK);
    }

    @GetMapping("/listar-todos")
    public ResponseEntity<Page<AssinaturaResponseDTO>> listarTodasAssinaturas(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(assinaturaService.listarTodasAssinaturas(token, size, page), HttpStatus.OK);
    }

    @GetMapping("/listar-id/{idAssinatura}")
    public ResponseEntity<AssinaturaResponseDTO> listarAssinaturaPorId(
            @RequestHeader("Authorization") String token,
            @PathVariable("idAssinatura") Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(assinaturaService.listarAssinaturaPorId(token, id, size, page), HttpStatus.OK);
    }
}
