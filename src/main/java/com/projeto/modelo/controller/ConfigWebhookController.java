package com.projeto.modelo.controller;

import com.projeto.modelo.controller.dto.request.ConfigWebhookRequestDTO;
import com.projeto.modelo.controller.dto.response.ConfigWebhookResponseDTO;
import com.projeto.modelo.service.ConfigWebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class ConfigWebhookController {

    @Autowired
    private ConfigWebhookService configWebhookService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/criar")
    public ResponseEntity<ConfigWebhookResponseDTO> criarWebhook(@RequestBody ConfigWebhookRequestDTO dto) {
        ConfigWebhookResponseDTO responseDTO = configWebhookService.criarWebhook(dto);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listar")
    public ResponseEntity<ConfigWebhookResponseDTO> listarWebhook() {
        ConfigWebhookResponseDTO responseDTO = configWebhookService.listarWebhook();
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/atualizar/{id}")
    public ResponseEntity<ConfigWebhookResponseDTO> atualizarWebhook(@PathVariable Long id, @RequestBody ConfigWebhookRequestDTO dto) {
        ConfigWebhookResponseDTO responseDTO = configWebhookService.atualizarWebhook(dto, id);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
