package com.projeto.modelo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.modelo.controller.dto.request.CadastrarProdutoDTO;
import com.projeto.modelo.controller.dto.response.ProdutoResponseDTO;
import com.projeto.modelo.controller.dto.response.ProdutoResponseListDTO;
import com.projeto.modelo.model.enums.ProdutoStatus;
import com.projeto.modelo.service.ProdutoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/cadastrar")
    public ResponseEntity<ProdutoResponseDTO> cadastrarProduto(
            @RequestPart(name = "dados") String dados,
            @RequestPart(name = "imagens", required = false) List<MultipartFile> imagens) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        CadastrarProdutoDTO dadosRecebidos = objectMapper.readValue(dados, CadastrarProdutoDTO.class);

        return new ResponseEntity<>(produtoService.cadastrarProduto(dadosRecebidos, imagens), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listar-todos")
    public ResponseEntity<Page<ProdutoResponseListDTO>> listarTodosProdutos(
            @RequestParam(name = "nome_busca", required = false) String nomeBusca,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return new ResponseEntity<>(produtoService.listarTodosOsProdutos(nomeBusca, page, size), HttpStatus.OK);
    }

    @GetMapping("/listar-por-id/{produtoId}")
    public ResponseEntity<ProdutoResponseDTO> listarTodosProdutos(
            @PathVariable Long produtoId
    ) {
        return new ResponseEntity<>(produtoService.listarProdutoPorId(produtoId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/alterar-status/{produtoId}/{status}")
    public ResponseEntity<Boolean> listarTodosProdutos(
            @PathVariable Long produtoId,
            @PathVariable ProdutoStatus status
    ) {
        return new ResponseEntity<>(produtoService.alterarStatusProduto(produtoId, status), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/editar-produto/{produtoId}")
    public ResponseEntity<ProdutoResponseDTO> editarProduto(
            @PathVariable Long produtoId,
            @RequestPart(name = "dados") String dados,
            @RequestPart(name = "imagens", required = false) List<MultipartFile> imagens) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        CadastrarProdutoDTO dadosRecebidos = objectMapper.readValue(dados, CadastrarProdutoDTO.class);
        return new ResponseEntity<>(produtoService.editarProduto(produtoId, dadosRecebidos, imagens), HttpStatus.OK);
    }
}
