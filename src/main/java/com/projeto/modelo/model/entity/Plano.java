package com.projeto.modelo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projeto.modelo.model.enums.Peridiocidade;
import com.projeto.modelo.model.enums.TipoPrimeiraParcela;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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
}