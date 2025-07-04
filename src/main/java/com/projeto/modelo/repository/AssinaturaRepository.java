package com.projeto.modelo.repository;

import com.projeto.modelo.model.entity.Assinatura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssinaturaRepository extends JpaRepository<Assinatura, Long> {

    @Query(
            value = "SELECT a.* FROM assinaturas a JOIN vendas v ON a.id_venda = v.id WHERE v.id_cliente = :idCliente",
            countQuery = "SELECT COUNT(*) FROM assinaturas a JOIN vendas v ON a.id_venda = v.id WHERE v.id_cliente = :idCliente",
            nativeQuery = true
    )
    Page<Assinatura> buscarAssinaturasPorCliente(@Param("idCliente") Long idCliente, Pageable pageable);

    @Query(
            value = "SELECT a.* FROM assinaturas a JOIN vendas v ON a.id_venda = v.id WHERE v.id_vendedor = :idVendedor",
            countQuery = "SELECT COUNT(*) FROM assinaturas a JOIN vendas v ON a.id_venda = v.id WHERE v.id_vendedor = :idVendedor",
            nativeQuery = true
    )
    Page<Assinatura> buscarAssinaturasPorVendedor(@Param("idVendedor") Long idVendedor, Pageable pageable);

}
