package com.projeto.modelo.service.imp;

import com.projeto.modelo.configuracao.JwtUtil;
import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.controller.dto.request.AssinaturaRequestDTO;
import com.projeto.modelo.controller.dto.response.AssinaturaResponseDTO;
import com.projeto.modelo.mapper.AssinaturaMapper;
import com.projeto.modelo.model.entity.Assinatura;
import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.model.enums.PermissaoStatus;
import com.projeto.modelo.repository.AssinaturaRepository;
import com.projeto.modelo.repository.UsuarioRepository;
import com.projeto.modelo.service.AssinaturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AssinaturaServiceImp implements AssinaturaService {

    @Autowired
    private AssinaturaRepository assinaturaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AssinaturaMapper assinaturaMapper;


    @Override
    public AssinaturaResponseDTO criarAssinatura(AssinaturaRequestDTO dto) {
        Assinatura assinatura = assinaturaMapper.toEntity(dto);
        assinaturaRepository.save(assinatura);
        return assinaturaMapper.toResponseDTO(assinatura);
    }

    @Override
    public AssinaturaResponseDTO atualizarAssinatura(Long idAssinatura, AssinaturaRequestDTO dto) {
        Assinatura assinatura = assinaturaRepository.findById(idAssinatura).orElseThrow(() -> new ExcecoesCustomizada("Assinatura não encontrada!", HttpStatus.NOT_FOUND));
        assinaturaMapper.atualizarAssinatura(assinatura, dto);
        assinaturaRepository.save(assinatura);
        return assinaturaMapper.toResponseDTO(assinatura);
    }

    @Override
    public Page<AssinaturaResponseDTO> listarTodasAssinaturas(String token, int size, int page) {
        Usuario usuarioSolicitante = usuarioRepository.findByEmail(jwtUtil.extractUsername(token.substring(7))).orElseThrow(() -> new ExcecoesCustomizada("Usuário não encontrado!", HttpStatus.NOT_FOUND));

        if (usuarioSolicitante.getPermissao().equals(PermissaoStatus.CLIENTE)) {
            return assinaturaMapper.toResponseDTOList(assinaturaRepository.buscarAssinaturasPorCliente(usuarioSolicitante.getId(), PageRequest.of(page, size)));
        } else if (usuarioSolicitante.getPermissao().equals(PermissaoStatus.VENDEDOR)) {
            return assinaturaMapper.toResponseDTOList(assinaturaRepository.buscarAssinaturasPorVendedor(usuarioSolicitante.getId(), PageRequest.of(page, size)));
        } else {
            return assinaturaMapper.toResponseDTOList(assinaturaRepository.findAll(PageRequest.of(page, size)));
        }
    }

    @Override
    public AssinaturaResponseDTO listarAssinaturaPorId(String token, Long assinaturaId, int size, int page) {
        Usuario usuarioSolicitante = usuarioRepository.findByEmail(jwtUtil.extractUsername(token.substring(7))).orElseThrow(() -> new ExcecoesCustomizada("Usuário não encontrado!", HttpStatus.NOT_FOUND));
        Assinatura assinatura = assinaturaRepository.findById(assinaturaId).orElseThrow(() -> new ExcecoesCustomizada("Assinatura não encontrada!", HttpStatus.NOT_FOUND));

        PermissaoStatus permissao = usuarioSolicitante.getPermissao();
        Long usuarioId = usuarioSolicitante.getId();
        Long clienteId = assinatura.getCliente().getId();
        Long vendedorId = assinatura.getVenda().getVendedor().getId();

        if (permissao.equals(PermissaoStatus.ADMIN) ||
            permissao.equals(PermissaoStatus.FUNCIONARIO) ||
            usuarioId.equals(vendedorId) ||
            usuarioId.equals(clienteId)) {
            return assinaturaMapper.toResponseDTO(assinatura);
        } else {
            throw new ExcecoesCustomizada("Você não tem permissão pra ver esse registro!", HttpStatus.UNAUTHORIZED);
        }
    }
}
