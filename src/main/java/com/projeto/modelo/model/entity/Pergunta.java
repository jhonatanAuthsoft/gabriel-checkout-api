package com.projeto.modelo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "perguntas")
public class Pergunta {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String pergunta;
    private String resposta;
    @JoinColumn(name = "checkout_id")
    @JsonIgnore
    @ManyToOne
    private CheckoutProduto checkoutProduto;
}