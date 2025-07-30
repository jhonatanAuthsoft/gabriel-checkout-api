package com.projeto.modelo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projeto.modelo.model.enums.ProdutoStatus;
import com.projeto.modelo.model.enums.TipoDesconto;
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
@Table(name = "cupons")
public class Cupom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "codigo_cupom")
    private String codigoCupom;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_desconto")
    private TipoDesconto tipoDesconto;
    private BigDecimal valor;
    private String url;
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