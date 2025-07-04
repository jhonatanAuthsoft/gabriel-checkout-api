package com.projeto.modelo.service;

import com.projeto.modelo.controller.dto.request.AssinaturaRequestDTO;
import com.projeto.modelo.controller.dto.response.AssinaturaResponseDTO;
import org.springframework.data.domain.Page;

public interface AssinaturaService {

    AssinaturaResponseDTO criarAssinatura(AssinaturaRequestDTO dto);

    AssinaturaResponseDTO atualizarAssinatura(Long idAssinatura, AssinaturaRequestDTO dto);

    Page<AssinaturaResponseDTO> listarTodasAssinaturas(String token, int size, int page);

    AssinaturaResponseDTO listarAssinaturaPorId(String token, Long assinaturaId, int size, int page);
}
