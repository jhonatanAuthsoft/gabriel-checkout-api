package com.projeto.modelo.service.imp;

import com.projeto.modelo.configuracao.JwtUtil;
import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.controller.dto.request.AtualizarPedidoAdmin;
import com.projeto.modelo.controller.dto.request.AtualizarVendaDTO;
import com.projeto.modelo.controller.dto.request.CriarVendaRequestDTO;
import com.projeto.modelo.controller.dto.response.VendaResponseDTO;
import com.projeto.modelo.mapper.VendaMapper;
import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.model.entity.Venda;
import com.projeto.modelo.model.enums.PermissaoStatus;
import com.projeto.modelo.model.enums.StatusPagamento;
import com.projeto.modelo.model.enums.StatusVenda;
import com.projeto.modelo.repository.UsuarioRepository;
import com.projeto.modelo.repository.VendaRepository;
import com.projeto.modelo.service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VendaServiceImp implements VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Value("${token.system}")
    private String systemToken;

    @Autowired
    private VendaMapper vendaMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Long criarVenda(String token, CriarVendaRequestDTO dto, Boolean primeiraVenda) {

        if (token != null && !token.equals(systemToken)) {
            throw new ExcecoesCustomizada("Apenas o Sistema pode usar esse endpoint", HttpStatus.UNAUTHORIZED);
        }

        Venda venda = vendaMapper.toEntity(dto, primeiraVenda);
        return vendaRepository.save(venda).getId();
    }

    @Override
    public Page<VendaResponseDTO> listarTodasVendas(String token, int size, int page) {
        Usuario usuario = this.usuarioRepository.findByEmail(jwtUtil.extractUsername(token.substring(7))).orElseThrow(() -> new ExcecoesCustomizada("Usuário não encontrada!", HttpStatus.NOT_FOUND));

        if (usuario.getPermissao().equals(PermissaoStatus.ADMIN) || usuario.getPermissao().equals(PermissaoStatus.FUNCIONARIO)) {
            return vendaMapper.toResponseListDTO(vendaRepository.findAll(PageRequest.of(page, size)));
        } else if (usuario.getPermissao().equals(PermissaoStatus.VENDEDOR)) {
            return vendaMapper.toResponseListDTO(vendaRepository.listarTodosPorVendedor(PageRequest.of(page, size), usuario.getId()));
        } else {
            return vendaMapper.toResponseListDTO(vendaRepository.listarTodosPorClient(PageRequest.of(page, size), usuario.getId()));
        }
    }

    @Override
    public VendaResponseDTO listarVendaPorId(String token, Long id) {
        Venda venda = vendaRepository.findById(id).orElseThrow(() -> new ExcecoesCustomizada("Venda não encontrada!", HttpStatus.NOT_FOUND));
        Usuario usuarioSolicitante = usuarioRepository.findByEmail(jwtUtil.extractUsername(token.substring(7))).orElseThrow(() -> new ExcecoesCustomizada("Usuário não encontrado!", HttpStatus.NOT_FOUND));

        if (usuarioSolicitante.getPermissao().equals(PermissaoStatus.CLIENTE) && !usuarioSolicitante.getId().equals(venda.getCliente().getId())) {
            throw new ExcecoesCustomizada("Você não tem permissão pra ver esse registro", HttpStatus.UNAUTHORIZED);
        }

        return vendaMapper.toResponseDTO(venda);
    }

    @Override
    public VendaResponseDTO atualizarPedidoAdmin(Long idVenda, AtualizarPedidoAdmin dto) {
        Venda vendaEditada = vendaRepository.findById(idVenda).orElseThrow(() -> new ExcecoesCustomizada("Venda não encontrada!", HttpStatus.NOT_FOUND));
        vendaMapper.atualizarPedido(vendaEditada, dto);
        vendaRepository.save(vendaEditada);
        return vendaMapper.toResponseDTO(vendaEditada);
    }

    public VendaResponseDTO solicitarReembolso(String token, Long id) {
        Usuario usuarioSolicitante = this.usuarioRepository.findByEmail(jwtUtil.extractUsername(token.substring(7))).orElseThrow(() -> new ExcecoesCustomizada("Usuário não encontrada!", HttpStatus.NOT_FOUND));
        Venda venda = vendaRepository.findById(id).orElseThrow(() -> new ExcecoesCustomizada("Venda não encontrada!", HttpStatus.NOT_FOUND));

        if (!usuarioSolicitante.getId().equals(venda.getCliente().getId())) {
            throw new ExcecoesCustomizada("Usuário não autorizado a solicitar reembolso nesse pedido", HttpStatus.UNAUTHORIZED);
        }

        venda.setStatusVenda(StatusVenda.CANCELADO);
        venda.setStatusPagamento(StatusPagamento.REEMBOLSO_SOLICITADO);
        return vendaMapper.toResponseDTO(vendaRepository.save(venda));
    }

    @Override
    public void gerarPagamento(Long idVenda, AtualizarVendaDTO dto) {
        Venda venda = vendaRepository.findById(idVenda).orElseThrow(() -> new ExcecoesCustomizada("Venda não encontrada!", HttpStatus.NOT_FOUND));
        vendaMapper.gerarPagamento(venda, dto);
        vendaRepository.save(venda);
    }

    @Override
    public void confirmarPagamento(String verificador, StatusPagamento statusPagamento, StatusVenda statusVenda, LocalDateTime dataPagamento) {
        Venda venda = vendaRepository.buscarPorVerificador(verificador).orElseThrow(() -> new ExcecoesCustomizada("Venda não encontrada!", HttpStatus.NOT_FOUND));
        vendaMapper.confirmarPagamento(venda, statusPagamento, statusVenda, dataPagamento);
        vendaRepository.save(venda);
    }
}
