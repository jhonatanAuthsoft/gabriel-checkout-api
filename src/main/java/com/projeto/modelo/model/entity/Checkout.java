package com.projeto.modelo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projeto.modelo.model.entity.produto.Exibicoes;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "checkouts")
public class Checkout {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Embedded
    Exibicoes exibicoes;

    @OneToMany(mappedBy = "checkout", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Pergunta> perguntas;

    @JoinColumn(name = "produto_id")
    @JsonIgnore
    @OneToOne
    private Produto produto;
}