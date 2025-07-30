package com.projeto.modelo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projeto.modelo.model.enums.Peridiocidade;
import com.projeto.modelo.model.enums.ProdutoStatus;
import com.projeto.modelo.model.enums.TipoPrimeiraParcela;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "planos")
public class Plano {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nome;
    @Enumerated(EnumType.STRING)
    private Peridiocidade peridiocidade;
    private String descricao;
    private BigDecimal preco;
    private Boolean gratis;
    @Enumerated(EnumType.STRING)
    private TipoPrimeiraParcela primeiraParcela;
    private String recorrencia;
    private String sku;
    @JoinColumn(name = "produto_id")
    @JsonIgnore
    @ManyToOne
    private Produto produto;

    @Enumerated(EnumType.STRING)
    private ProdutoStatus status;

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "data_delecao")
    private LocalDateTime dataDelecao;
}