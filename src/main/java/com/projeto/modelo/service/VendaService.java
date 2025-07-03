package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.AtualizarPedidoAdmin;
import com.projeto.modelo.controller.dto.request.AtualizarVendaDTO;
import com.projeto.modelo.controller.dto.request.CriarVendaRequestDTO;
import com.projeto.modelo.controller.dto.response.VendaResponseDTO;
import com.projeto.modelo.model.enums.StatusPagamento;
import com.projeto.modelo.model.enums.StatusVenda;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

public interface VendaService {

    Long criarVenda(String token, CriarVendaRequestDTO dto, Boolean primeiraVenda);

    Page<VendaResponseDTO> listarTodasVendas(String token, int size, int page);

    VendaResponseDTO listarVendaPorId(String token, Long id);

    VendaResponseDTO atualizarPedidoAdmin(Long idVenda, AtualizarPedidoAdmin dto);

    VendaResponseDTO solicitarReembolso(String token, Long id);

    void gerarPagamento(Long idVenda, AtualizarVendaDTO dto);

    void confirmarPagamento(String verificador, StatusPagamento statusPagamento, StatusVenda statusVenda, LocalDateTime dataPagamento);
}
