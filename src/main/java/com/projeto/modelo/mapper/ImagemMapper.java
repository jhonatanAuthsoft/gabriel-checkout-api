package com.projeto.modelo.mapper;

import com.projeto.modelo.model.entity.Imagem;
import com.projeto.modelo.model.entity.Produto;
import com.projeto.modelo.model.entity.produto.MapeamentoImagem;
import com.projeto.modelo.service.AwsS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ImagemMapper {

    @Autowired
    private AwsS3Service awsS3Service;

    private MapeamentoImagem resolveMapeamentoAtual(List<MapeamentoImagem> mapeamentos, String nomeArquivo) {
        if (mapeamentos == null || nomeArquivo == null) {
            return null;
        }

        return mapeamentos.stream()
                .filter(m -> nomeArquivo.equals(m.nomeArquivo()))
                .findFirst()
                .orElse(null);
    }

    public void adicionarNovasImagens(List<MapeamentoImagem> mapeamentoImagens, List<MultipartFile> novasImagens, Produto produto) throws IOException {
        if (novasImagens == null || novasImagens.isEmpty()) return;

        List<Imagem> imagens = this.toEntity(mapeamentoImagens, novasImagens, produto);

        // Adiciona as novas imagens à lista existente
        produto.getImagens().addAll(imagens);
    }


    public List<Imagem> toEntity(List<MapeamentoImagem> mapeamentoImagens, List<MultipartFile> imagens, Produto produto) throws IOException {
        List<Imagem> imagensSalvas = new ArrayList<>();

        for (MultipartFile imagemAtual : imagens) {
            MapeamentoImagem mapeamentoAtual = resolveMapeamentoAtual(mapeamentoImagens, imagemAtual.getOriginalFilename());

            if (mapeamentoAtual != null) {
                Imagem imagemCriada = Imagem.builder()
                        .nomeImagem(imagemAtual.getOriginalFilename())
                        .produto(produto)
                        .tipoImagem(mapeamentoAtual.tipo())
                        .tamanhoImagem(Long.valueOf(imagemAtual.getSize()))
                        .build();

                String path = "produtos/" + mapeamentoAtual.tipo();

                imagemCriada = awsS3Service.uploadFile(imagemAtual, path, imagemCriada);

                imagensSalvas.add(imagemCriada);
            }
        }

        return imagensSalvas;
    }


} 