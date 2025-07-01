package com.projeto.modelo.controller.dto.request;

import com.projeto.modelo.model.entity.Produto;
import com.projeto.modelo.model.entity.produto.MapeamentoImagem;
import lombok.Builder;

import java.util.List;

@Builder
public record CadastrarProdutoDTO(Produto dados,
                                  List<MapeamentoImagem> mapeamentoImagens,
                                  List<Long> imagensParaDeletar
) {

} 