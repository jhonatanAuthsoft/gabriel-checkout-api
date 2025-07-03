package com.projeto.modelo.repository;

import com.projeto.modelo.model.entity.Venda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    @Query(value = "SELECT * FROM vendas WHERE id_vendedor = :idVendedor",
            countQuery = "SELECT count(*) FROM venda WHERE id_vendedor = :idVendedor",
            nativeQuery = true)
    Page<Venda> listarTodosPorVendedor(Pageable pageable, @Param("idVendedor") Long idVendedor);

    @Query(value = "SELECT * FROM vendas WHERE id_cliente = :idCliente",
            countQuery = "SELECT count(*) FROM venda WHERE id_cliente = :idCliente",
            nativeQuery = true)
    Page<Venda> listarTodosPorClient(Pageable pageable, @Param("idCliente") Long idCliente);

    @Query(value = "SELECT * FROM vendas WHERE txid = :verificador OR codigo_solicitacao = :verificador", nativeQuery = true)
    Optional<Venda> buscarPorVerificador(@Param("verificador") String verificador);
}
