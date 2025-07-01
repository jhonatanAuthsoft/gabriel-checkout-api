package com.projeto.modelo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projeto.modelo.model.enums.TipoImagem;
import com.projeto.modelo.service.AwsS3Service;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "imagens")
public class Imagem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore
    @JoinColumn(name = "produto_id", nullable = false)
    @ManyToOne
    private Produto produto;

    @Column(name = "nome_imagem", nullable = false)
    private String nomeImagem;

    @Column(name = "tamanho_imagem", nullable = false)
    private Long tamanhoImagem;

    @Column(name = "caminho_imagem", nullable = false)
    private String caminhoImagem;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_imagem", nullable = false)
    private TipoImagem tipoImagem;

    @Transient
    private String signedUrl;

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false, nullable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;
}
