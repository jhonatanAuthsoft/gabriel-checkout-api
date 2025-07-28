package com.projeto.modelo.repository;

import com.projeto.modelo.controller.dto.response.dashboard.*;
import com.projeto.modelo.model.entity.Venda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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

    @Query(value = "SELECT * FROM vendas WHERE txid = :verificador OR codigo_solicitacao = :verificador OR id_pagamento_credito = :verificador", nativeQuery = true)
    Optional<Venda> buscarPorVerificador(@Param("verificador") String verificador);

    @Query(value =
            "SELECT " +
            "COALESCE((SELECT SUM(valor_pago) FROM vendas v1 WHERE v1.data_pagamento >= :dataInicioAtual AND v1.data_pagamento < :dataFimAtual AND v1.status_venda = 'FINALIZADO'), 0), " +
            "COALESCE((SELECT SUM(valor_pago) FROM vendas v2 WHERE v2.data_pagamento >= :dataInicioAnterior AND v2.data_pagamento < :dataFimAnterior AND v2.status_venda = 'FINALIZADO'), 0), " +
            "COALESCE((SELECT COUNT(*) FROM vendas v3 WHERE v3.data_pagamento >= :dataInicioAtual AND v3.data_pagamento < :dataFimAtual AND v3.status_venda = 'FINALIZADO'), 0), " +
            "COALESCE((SELECT COUNT(*) FROM vendas v4 WHERE v4.data_pagamento >= :dataInicioAnterior AND v4.data_pagamento < :dataFimAnterior AND v4.status_venda = 'FINALIZADO'), 0)",
            nativeQuery = true)
    VendasPeriodoResponseDB getVendasPorPeriodo(@Param("dataInicioAtual") LocalDateTime dataInicioAtual,
                                                @Param("dataFimAtual") LocalDateTime dataFimAtual,
                                                @Param("dataInicioAnterior") LocalDateTime dataInicioAnterior,
                                                @Param("dataFimAnterior") LocalDateTime dataFimAnterior);


    @Query(
            value = "SELECT " +
                    "CAST(v.produto_id AS BIGINT) AS idProduto, " +
                    "p.nome AS nome, " +
                    "COUNT(v.id) AS totalVendas, " +
                    "ROUND(COUNT(v.id) * 100.0 / NULLIF((SELECT COUNT(v2.id) FROM vendas v2 WHERE v2.status_venda = 'FINALIZADO'), 0), 2) AS percentualDasVendas " +
                    "FROM vendas v " +
                    "JOIN produtos p ON v.produto_id = p.id " +
                    "WHERE v.status_venda = 'FINALIZADO' " +
                    "GROUP BY v.produto_id, p.nome " +
                    "ORDER BY totalVendas DESC",
            nativeQuery = true)
    List<ProdutosMaisVendidos> getProdutosMaisVendidos();

    @Query(value = "SELECT v.id AS idVenda, p.nome AS nome, CAST(v.data_reembolso AS TIMESTAMP) AS dataReembolso " +
                   "FROM vendas v JOIN produtos p ON v.produto_id = p.id " +
                   "WHERE v.data_reembolso IS NOT NULL AND v.data_reembolso >= NOW() - INTERVAL '30 days' " +
                   "ORDER BY v.data_reembolso DESC", nativeQuery = true)
    List<Object[]> getReembolsosUltimos30Dias();

    @Query(value = "SELECT EXTRACT(MONTH FROM v.data_pagamento) AS mes, COUNT(*) AS total " +
                   "FROM vendas v WHERE v.status_pagamento = 'REEMBOLSADO' AND v.data_pagamento IS NOT NULL " +
                   "AND v.data_pagamento >= :dataInicio AND v.data_pagamento <= :dataFim " +
                   "GROUP BY mes ORDER BY mes", nativeQuery = true)
    List<MesAMesChargebackQuery> getMesAMesChargeback(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    @Query(value = "SELECT COUNT(*) FROM vendas v WHERE v.status_pagamento = 'REEMBOLSADO' " +
                   "AND v.data_pagamento >= :dataInicio AND v.data_pagamento <= :dataFim", nativeQuery = true)
    Integer getTotalChargeback(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    @Query(value = "SELECT EXTRACT(MONTH FROM v.data_pagamento) AS mes, SUM(v.valor_pago) AS totalVenda, CAST(0.0 AS NUMERIC) AS totalCampanha, CAST(0.0 AS NUMERIC) AS totalLink FROM vendas v WHERE v.status_venda = 'FINALIZADO' AND v.data_pagamento >= :dataInicio AND v.data_pagamento <= :dataFim GROUP BY mes ORDER BY mes", nativeQuery = true)
    List<MesAMesVendasPeriodoQuery> getMesAMesVendasPorPeriodo(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    @Query(value = "SELECT * FROM vendas WHERE status_venda = 'FINALIZADO' AND data_pagamento >= :dataInicio AND data_pagamento <= :dataFim ORDER BY id_cliente, data_pagamento", nativeQuery = true)
    List<Venda> findVendasFinalizadasNoPeriodo(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    @Query(value = "SELECT * FROM vendas WHERE status_venda = 'CANCELADO' AND data_pagamento >= :dataInicio AND data_pagamento <= :dataFim ORDER BY id_cliente, data_pagamento", nativeQuery = true)
    List<Venda> findVendasCanceladas(@Param("dataInicio") LocalDateTime dataInicio, @Param("dataFim") LocalDateTime dataFim);

    @Query(value = "SELECT COUNT(v) FROM vendas v WHERE produto_id = :idProduto", nativeQuery = true)
    Long contarVendasPorProduto(@Param("idProduto") Long idProduto);
}
