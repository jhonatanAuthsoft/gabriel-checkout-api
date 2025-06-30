package com.projeto.modelo.service.imp;

import com.projeto.modelo.controller.dto.request.CadastrarProdutoDTO;
import com.projeto.modelo.controller.dto.response.ProdutoResponseDTO;
import com.projeto.modelo.controller.dto.response.ProdutoResponseListDTO;
import com.projeto.modelo.repository.ProdutoRepository;
import com.projeto.modelo.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ProdutoServiceImp implements ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Override
    public ProdutoResponseDTO cadastrarProduto(CadastrarProdutoDTO cadastrarProdutoDTO) {
        return null;
    }

    @Override
    public ProdutoResponseDTO editarProduto(CadastrarProdutoDTO cadastrarProdutoDTO) {
        return null;
    }

    @Override
    public void deletarProduto(Long productId) {

    }

    @Override
    public Page<ProdutoResponseListDTO> listarTodosOsProdutos(String searchText, int page, int size) {
        return null;
    }
}
