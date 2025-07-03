package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.PagamentoRequestDTO;

public interface PagamentoService {
    Object pagarVenda(PagamentoRequestDTO dto);
}
