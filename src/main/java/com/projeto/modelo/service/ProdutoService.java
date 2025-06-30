package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.CadastrarProdutoDTO;
import com.projeto.modelo.controller.dto.response.ProdutoResponseDTO;
import com.projeto.modelo.controller.dto.response.ProdutoResponseListDTO;
import org.springframework.data.domain.Page;

public interface ProdutoService {

    ProdutoResponseDTO cadastrarProduto(CadastrarProdutoDTO cadastrarProdutoDTO);

    ProdutoResponseDTO editarProduto(CadastrarProdutoDTO cadastrarProdutoDTO);

    void deletarProduto(Long productId);

    Page<ProdutoResponseListDTO> listarTodosOsProdutos(String searchText, int page, int size);
}
