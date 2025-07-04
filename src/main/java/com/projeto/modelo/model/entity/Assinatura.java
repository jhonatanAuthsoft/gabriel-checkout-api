package com.projeto.modelo.model.entity;

import com.projeto.modelo.model.enums.MetodoPagamento;
import com.projeto.modelo.model.enums.StatusAssinatura;
import com.projeto.modelo.model.enums.TipoCobranca;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "assinaturas")
public class Assinatura {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_produto")
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "id_plano")
    private Plano plano;

    @OneToOne
    @JoinColumn(name = "id_venda")
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Usuario cliente;

    @Column(name = "tipo_cobranca")
    @Enumerated(EnumType.STRING)
    private TipoCobranca tipoCobranca;

    @Column(name = "metodo_pagamento")
    @Enumerated(EnumType.STRING)
    private MetodoPagamento metodoPagamento;

    @Column(name = "status_assinatura")
    @Enumerated(EnumType.STRING)
    private StatusAssinatura statusAssinatura;

    @CreationTimestamp()
    @Column(name = "data_inicio", updatable = false)
    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    @Column(name = "data_cancelamento_datado")
    private LocalDateTime dataCancelamentoDatado;

}
