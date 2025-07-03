package com.projeto.modelo.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.projeto.modelo.model.enums.ProdutoStatus;
import com.projeto.modelo.model.enums.TipoDesconto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

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
}