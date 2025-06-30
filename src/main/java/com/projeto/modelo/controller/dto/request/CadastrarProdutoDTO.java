package com.projeto.modelo.controller.dto.request;

import com.projeto.modelo.model.entity.produto.MapeamentoImagem;
import com.projeto.modelo.model.entity.Produto;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record CadastrarProdutoDTO(Produto dados,
                                  List<MapeamentoImagem> mapeamentoImagens,
                                  List<MultipartFile> imagens) {

} 