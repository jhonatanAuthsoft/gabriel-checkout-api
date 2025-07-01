package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.CadastrarProdutoDTO;
import com.projeto.modelo.controller.dto.response.ProdutoResponseDTO;
import com.projeto.modelo.controller.dto.response.ProdutoResponseListDTO;
import com.projeto.modelo.model.enums.ProdutoStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProdutoService {

    ProdutoResponseDTO cadastrarProduto(CadastrarProdutoDTO cadastrarProdutoDTO, List<MultipartFile> imagens) throws IOException;

    ProdutoResponseDTO editarProduto(Long idProduto, CadastrarProdutoDTO cadastrarProdutoDTO, List<MultipartFile> imagens) throws IOException;

    Boolean alterarStatusProduto(Long productId, ProdutoStatus produtoStatus);

    ProdutoResponseDTO listarProdutoPorId(Long produtoId);

    Page<ProdutoResponseListDTO> listarTodosOsProdutos(String searchText, int page, int size);
}
