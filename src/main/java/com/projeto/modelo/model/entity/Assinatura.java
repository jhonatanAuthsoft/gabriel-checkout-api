package com.projeto.modelo.model.entity;

import com.projeto.modelo.model.enums.MetodoPagamento;
import com.projeto.modelo.model.enums.StatusAssinatura;
import com.projeto.modelo.model.enums.TipoCobranca;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

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

    @ManyToMany
    @JoinTable(
            name = "assinatura_produto", // nome da tabela de junção
            joinColumns = @JoinColumn(name = "assinatura_id"), // chave estrangeira da entidade atual (provavelmente Venda)
            inverseJoinColumns = @JoinColumn(name = "produto_id") // chave estrangeira da entidade relacionada (Produto)
    )
    private List<Produto> produtos;

    @ManyToMany
    @JoinTable(
            name = "assinatura_plano", // nome da tabela de junção
            joinColumns = @JoinColumn(name = "assinatura_id"), // chave estrangeira da entidade atual (provavelmente Venda)
            inverseJoinColumns = @JoinColumn(name = "plano_id") // chave estrangeira da entidade relacionada (Produto)
    )
    private List<Plano> planos;

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
