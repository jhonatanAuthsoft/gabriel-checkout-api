package com.projeto.modelo.controller;

import com.projeto.modelo.controller.dto.request.AtualizarPedidoAdmin;
import com.projeto.modelo.controller.dto.request.CriarVendaRequestDTO;
import com.projeto.modelo.controller.dto.response.VendaResponseDTO;
import com.projeto.modelo.service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/venda")
@RestController
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @PostMapping("/criar")
    public ResponseEntity<Long> criarVenda(@RequestHeader(value = "token-sistema") String token, @RequestBody CriarVendaRequestDTO dto) {
        return new ResponseEntity<>(vendaService.criarVenda(token, dto, true), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<VendaResponseDTO>> listarVendas(@RequestHeader(value = "Authorization") String token,
                                                               @RequestParam(name = "page", defaultValue = "0") int page,
                                                               @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(vendaService.listarTodasVendas(token, size, page), HttpStatus.OK);
    }

    @GetMapping("/listar-id/{idVenda}")
    public ResponseEntity<VendaResponseDTO> listarVendaPorId(@RequestHeader(value = "Authorization") String token, @PathVariable Long idVenda) {
        return new ResponseEntity<>(vendaService.listarVendaPorId(token, idVenda), HttpStatus.OK);
    }

    @PatchMapping("/solicitar-reembolso/{idVenda}")
    public ResponseEntity<VendaResponseDTO> solicitarReembolso(@RequestHeader(value = "Authorization") String token, @PathVariable Long idVenda) {
        return new ResponseEntity<>(vendaService.solicitarReembolso(token, idVenda), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/editar/{idVenda}")
    public ResponseEntity<VendaResponseDTO> atualizarPedidoAdmin(@RequestBody AtualizarPedidoAdmin dto, @PathVariable Long idVenda) {
        return new ResponseEntity<>(vendaService.atualizarPedidoAdmin(idVenda, dto), HttpStatus.OK);
    }


}
