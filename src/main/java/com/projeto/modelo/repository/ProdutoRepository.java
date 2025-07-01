package com.projeto.modelo.repository;

import com.projeto.modelo.model.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("""
            SELECT p FROM Produto p
            WHERE LOWER(p.dadosProduto.dadosGerais.nome) LIKE LOWER(CONCAT('%', :nomeBusca, '%'))
               OR LOWER(p.dadosProduto.dadosGerais.codigo) LIKE LOWER(CONCAT('%', :nomeBusca, '%'))
               OR LOWER(p.dadosProduto.dadosGerais.codigoSku) LIKE LOWER(CONCAT('%', :nomeBusca, '%'))
            """)
    Page<Produto> buscarPorNomeOuCodigoOuSku(@Param("nomeBusca") String nomeBusca, Pageable pageable);
}
