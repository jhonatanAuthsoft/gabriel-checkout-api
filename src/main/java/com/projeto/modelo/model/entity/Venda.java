package com.projeto.modelo.model.entity;

import com.projeto.modelo.model.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Produto produto;

    private BigDecimal valorPago;

    private String txid;

    private String codigoSolicitacao;

    @ManyToOne
    private Cupom cupomUsado;

    @ManyToOne
    private Plano plano;

    @Column(name = "origem_compra")
    @Enumerated(EnumType.STRING)
    private OrigemCompra origemCompra;

    @Column(name = "metodo_pagamento")
    @Enumerated(EnumType.STRING)
    private MetodoPagamento metodoPagamento;

    @Column(name = "status_pagamento")
    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento;

    @Column(name = "status_venda")
    @Enumerated(EnumType.STRING)
    private StatusVenda statusVenda;

    @Column(name = "tipo_recorrencia")
    @Enumerated(EnumType.STRING)
    private TipoCobranca tipoRecorrencia;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "id_vendedor")
    private Usuario vendedor;

    @CreationTimestamp
    @Column(name = "data_compra", nullable = false, updatable = false)
    private LocalDateTime dataCompra;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Column(name = "data_reembolso")
    private LocalDateTime dataReembolso;

    @UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;
}
