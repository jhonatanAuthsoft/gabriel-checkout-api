package com.projeto.modelo.service.imp;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.controller.dto.request.CadastrarProdutoDTO;
import com.projeto.modelo.controller.dto.response.ProdutoResponseDTO;
import com.projeto.modelo.controller.dto.response.ProdutoResponseListDTO;
import com.projeto.modelo.mapper.ImagemMapper;
import com.projeto.modelo.mapper.ProdutoMapper;
import com.projeto.modelo.model.entity.Imagem;
import com.projeto.modelo.model.entity.Produto;
import com.projeto.modelo.model.enums.ProdutoStatus;
import com.projeto.modelo.repository.ImagemRepository;
import com.projeto.modelo.repository.ProdutoRepository;
import com.projeto.modelo.service.AwsS3Service;
import com.projeto.modelo.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProdutoServiceImp implements ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoMapper produtoMapper;
    @Autowired
    private ImagemMapper imagemMapper;

    @Autowired
    private AwsS3Service awsS3Service;
    @Autowired
    private ImagemRepository imagemRepository;

    @Override
    public ProdutoResponseDTO cadastrarProduto(CadastrarProdutoDTO cadastrarProdutoDTO, List<MultipartFile> imagens) throws IOException {
        Produto produtoCriado = produtoMapper.toEntity(cadastrarProdutoDTO);
        produtoRepository.save(produtoCriado);

        if (!imagens.isEmpty()) {
            produtoCriado.setImagens(imagemMapper.toEntity(cadastrarProdutoDTO.mapeamentoImagens(), imagens, produtoCriado));
        }

        return produtoMapper.toResponseDTO(produtoCriado);
    }

    @Override
    public ProdutoResponseDTO editarProduto(Long idProduto, CadastrarProdutoDTO cadastrarProdutoDTO, List<MultipartFile> imagens) throws IOException {
        Produto produtoAEditar = produtoRepository.findById(idProduto).orElseThrow(() -> new ExcecoesCustomizada("Produto não encontrado!", HttpStatus.NOT_FOUND));
        produtoAEditar = produtoMapper.toEntity(cadastrarProdutoDTO);

        if (imagens != null && !imagens.isEmpty()) {
            produtoAEditar.setImagens(imagemMapper.toEntity(cadastrarProdutoDTO.mapeamentoImagens(), imagens, produtoAEditar));
        }

        if (cadastrarProdutoDTO.imagensParaDeletar() != null && !cadastrarProdutoDTO.imagensParaDeletar().isEmpty()) {
            for (Long imagemId : cadastrarProdutoDTO.imagensParaDeletar()) {
                Imagem imagemADeletar = imagemRepository.findById(imagemId).orElseThrow(() -> new ExcecoesCustomizada("Produto não encontrado!", HttpStatus.NOT_FOUND));
                awsS3Service.deleteFile(imagemId);
                produtoAEditar.getImagens().remove(imagemADeletar);
            }
        }

        produtoRepository.save(produtoAEditar);
        return produtoMapper.toResponseDTO(produtoAEditar);
    }

    @Override
    public Boolean alterarStatusProduto(Long productId, ProdutoStatus produtoStatus) {
        Produto produtoADesativar = produtoRepository.findById(productId).orElseThrow(() -> new ExcecoesCustomizada("Produto não encontrado!", HttpStatus.NOT_FOUND));
        produtoADesativar.setStatus(produtoStatus);
        produtoRepository.save(produtoADesativar);
        return true;
    }

    @Override
    public ProdutoResponseDTO listarProdutoPorId(Long produtoId) {
        return produtoMapper.toResponseDTO(produtoRepository.findById(produtoId).orElseThrow(() -> new ExcecoesCustomizada("Produto não encontrado!", HttpStatus.NOT_FOUND)));
    }

    @Override
    public Page<ProdutoResponseListDTO> listarTodosOsProdutos(String searchText, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (searchText == null || searchText.trim().isEmpty()) {
            return produtoMapper.toResponseListDTO(produtoRepository.findAll(pageable));
        }
        return produtoMapper.toResponseListDTO(produtoRepository.buscarPorNomeOuCodigoOuSku(searchText, pageable));
    }
}
