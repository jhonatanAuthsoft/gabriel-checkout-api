package com.projeto.modelo.service.imp;

import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.controller.dto.request.AssinaturaRequestDTO;
import com.projeto.modelo.controller.dto.request.AtualizarVendaDTO;
import com.projeto.modelo.controller.dto.request.PagamentoRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.boleto.BancoInterBoletoRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.boleto.BancoInterCallbackBoletoDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.boleto.BancoInterPagadorBoletoRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.pix.BancoInterPixRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.pix.BancoInterWebhookRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.pix.calback.BancoInterCallbackPixDTO;
import com.projeto.modelo.controller.dto.request.cielo.*;
import com.projeto.modelo.controller.dto.response.AssinaturaResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.BancoInterWebhookResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterBoletoPDFResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterCodigoBoletoResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.pix.BancoInterPixResponseDTO;
import com.projeto.modelo.controller.dto.response.cielo.CieloResponse;
import com.projeto.modelo.mapper.VendaMapper;
import com.projeto.modelo.model.entity.ConfigWebhook;
import com.projeto.modelo.model.entity.Usuario;
import com.projeto.modelo.model.entity.Venda;
import com.projeto.modelo.model.enums.*;
import com.projeto.modelo.repository.ConfigWebhookRepository;
import com.projeto.modelo.repository.VendaRepository;
import com.projeto.modelo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PagamentoServiceImp implements PagamentoService {

    @Autowired
    private BancoInterService bancoInterService;

    @Autowired
    private CieloService cieloService;

    @Autowired
    private ConfigWebhookRepository configWebhookRepository;

    @Autowired
    private VendaService vendaService;

    @Autowired
    private AssinaturaService assinaturaService;

    @Autowired
    private VendaRepository vendaRepository;


    @Value("${inter.duracao-pix}")
    private Integer DURACAO_PIX;

    @Value("${inter.dias-vencimento-boleto}")
    private Integer DIAS_VENCIMENTO_BOLETO;

    @Value("${cielo.soft-descriptor}")
    private String SOFT_DESCRIPTOR;
    @Autowired
    private VendaMapper vendaMapper;

    private String getBaseWebhookUrl() {
        Optional<ConfigWebhook> baseUrl = configWebhookRepository.findById(1L);

        if (baseUrl.isPresent()) {
            return baseUrl.get().getUrl();
        } else {
            // mandar e-mail dizendo que vai dar merda
        }
        return null;
    }

    private void validarCallback(MetodoPagamento metodoPagamento) {
        BancoInterWebhookResponseDTO webhook = consultarWebhooks(metodoPagamento);
        if (webhook == null) {
            this.cadastrarWebhooks(metodoPagamento, this.getBaseWebhookUrl());
        }
    }

    private AssinaturaResponseDTO criarAssinatura(AssinaturaRequestDTO dto) {
        return assinaturaService.criarAssinatura(dto);
    }

    @Override
    public BancoInterWebhookResponseDTO consultarWebhooks(MetodoPagamento tipoWebhook) {
        if (tipoWebhook.equals(MetodoPagamento.PIX)) {
            return bancoInterService.buscaWebhookPix();
        } else if (tipoWebhook.equals(MetodoPagamento.BOLETO)) {
            return bancoInterService.buscaWebhookBoleto();
        } else if (tipoWebhook.equals(MetodoPagamento.CARTAO)) {
            return null;
        } else {
            throw new ExcecoesCustomizada("Tipo de Webhook não encontrado!", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void cadastrarWebhooks(MetodoPagamento tipoWebhook, String baseUrl) {
        if (baseUrl == null) {
            return;
        }

        if (tipoWebhook.equals(MetodoPagamento.PIX)) {
            bancoInterService.cadastraWebhookPix(BancoInterWebhookRequestDTO.builder()
                    .webhookUrl(baseUrl + "pagamento/callback/pix")
                    .build());
        } else if (tipoWebhook.equals(MetodoPagamento.BOLETO)) {
            bancoInterService.cadastraWebhookPix(BancoInterWebhookRequestDTO.builder()
                    .webhookUrl(baseUrl + "pagamento/callback/boleto")
                    .build());
        } else if (tipoWebhook.equals(MetodoPagamento.CARTAO)) {
            bancoInterService.cadastraWebhookPix(BancoInterWebhookRequestDTO.builder()
                    .webhookUrl(baseUrl + "pagamento/callback/cartao")
                    .build());
        } else {
            throw new ExcecoesCustomizada("Tipo de Webhook não encontrado!", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public BancoInterPixResponseDTO pagarComPix(PagamentoRequestDTO dto) {
        this.validarCallback(dto.tipoCobranca());

        Venda venda = vendaRepository.findById(dto.idVenda()).orElseThrow(() -> new ExcecoesCustomizada("Venda não Encontrada!", HttpStatus.NOT_FOUND));
        Usuario cliente = venda.getCliente();

        if (venda.getStatusPagamento().equals(StatusPagamento.APROVADO) && venda.getStatusVenda().equals(StatusVenda.FINALIZADO)) {
            throw new ExcecoesCustomizada("Esse pedido já foi pago e finalizado!", HttpStatus.BAD_REQUEST);
        }

        BancoInterPixResponseDTO responsePix = bancoInterService.geraPix(BancoInterPixRequestDTO.builder()
                .expiracao(DURACAO_PIX)
                .devedorCpfCnpj(cliente.getCpf())
                .valorPagamento(venda.getValorPago())
                .build());
        vendaService.gerarPagamento(dto.idVenda(), AtualizarVendaDTO.builder()
                .txid(responsePix.getTxid())
                .build());

        return responsePix;
    }

    @Override
    public BancoInterBoletoPDFResponseDTO pagarComBoleto(PagamentoRequestDTO dto) {
        this.validarCallback(dto.tipoCobranca());

        Venda venda = vendaRepository.findById(dto.idVenda()).orElseThrow(() -> new ExcecoesCustomizada("Venda não Encontrada!", HttpStatus.NOT_FOUND));
        Usuario cliente = venda.getCliente();

        if (venda.getStatusPagamento().equals(StatusPagamento.APROVADO) && venda.getStatusVenda().equals(StatusVenda.FINALIZADO)) {
            throw new ExcecoesCustomizada("Esse pedido já foi pago e finalizado!", HttpStatus.BAD_REQUEST);
        }

        BancoInterCodigoBoletoResponseDTO responseBoleto = bancoInterService.gerarBoleto(BancoInterBoletoRequestDTO.builder()
                .valorPagamento(venda.getValorPago())
                .dataVencimento(LocalDate.now().plusDays(DIAS_VENCIMENTO_BOLETO))
                .pagador(BancoInterPagadorBoletoRequestDTO.builder()
                        .email(cliente.getEmail())
                        .ddd(cliente.getCelular().substring(0, 2))
                        .telefone(cliente.getCelular().substring(2))
                        .numeroResidencia(cliente.getEndereco().numeroResidencia())
                        .complementoEndereco(cliente.getEndereco().complementoEndereco())
                        .cpfCnpj(cliente.getCpf())
                        .nome(cliente.getNome())
                        .endereco(cliente.getEndereco().endereco())
                        .bairro(cliente.getEndereco().bairro())
                        .cidade(cliente.getEndereco().cidade())
                        .uf(cliente.getEndereco().uf())
                        .cep(cliente.getEndereco().cep())
                        .build())
                .build());

        vendaService.gerarPagamento(dto.idVenda(), AtualizarVendaDTO.builder()
                .codigoSolicitacao(responseBoleto.getCodigoSolicitacao())
                .build());

        return bancoInterService.consultarBoletoPdf(responseBoleto.getCodigoSolicitacao());
    }

    @Override
    public Boolean pagarComCartao(PagamentoRequestDTO dto) {
        Venda venda = vendaRepository.findById(dto.idVenda()).orElseThrow(() -> new ExcecoesCustomizada("Venda não Encontrada!", HttpStatus.NOT_FOUND));
        Usuario cliente = venda.getCliente();
        
        if (venda.getStatusPagamento().equals(StatusPagamento.APROVADO) && venda.getStatusVenda().equals(StatusVenda.FINALIZADO)) {
            throw new ExcecoesCustomizada("Esse pedido já foi pago e finalizado!", HttpStatus.BAD_REQUEST);
        }

        CieloResponse responseCielo = cieloService.pagarCielo(CieloReceberPagamentoCartao.builder()
                .merchantOrderId(dto.idVenda().toString())
                .proprietario(CieloProprietario.builder()
                        .nome(cliente.getNome())
                        .cpfCnpjRg(cliente.getCpf())
                        .email(dto.email())
                        .enderecoProprietario(Endereco.builder()
                                .logradouro(cliente.getEndereco().endereco())
                                .numero(cliente.getEndereco().numeroResidencia())
                                .complemento(cliente.getEndereco().complementoEndereco())
                                .cep(cliente.getEndereco().cep())
                                .cidade(cliente.getEndereco().cidade())
                                .estado(cliente.getEndereco().uf().toString())
                                .pais("BRA")
                                .build())
                        .enderecoCobranca(Endereco.builder()
                                .logradouro(cliente.getEndereco().endereco())
                                .numero(cliente.getEndereco().numeroResidencia())
                                .complemento(cliente.getEndereco().complementoEndereco())
                                .cep(cliente.getEndereco().cep())
                                .cidade(cliente.getEndereco().cidade())
                                .estado(cliente.getEndereco().uf().toString())
                                .pais("BRA")
                                .build())
                        .build())
                .pagamento(CieloPagamento.builder()
                        .tipoCartao(TipoCartao.CREDITO)
                        .valor(venda.getValorPago())
                        .moeda("BRL")
                        .pais("BRA")
                        .softDescriptor(SOFT_DESCRIPTOR)
                        .parcelas(dto.dadosCartao().parcelas())
                        .tipoParcelamento(TipoParcelamento.COMPRADOR)
                        .capture(true)
                        .cieloCartao(CieloCartao.builder()
                                .numeroCartao(dto.dadosCartao().numeroCartao())
                                .nomeImpresso(dto.dadosCartao().nomeImpresso())
                                .dataVencimento(dto.dadosCartao().dataVencimento())
                                .codigoSeguranca(dto.dadosCartao().codigoSeguranca())
                                .bandeiraCartao(dto.dadosCartao().bandeiraCartao())
                                .build())
                        .build())
                .build());

        vendaService.gerarPagamento(venda.getId(), AtualizarVendaDTO.builder()
                .idPagamentoCartao(responseCielo.pagamento().idPagamento())
                .build());

        return this.callbackCartao(responseCielo);
    }

    @Override
    public void callbackPix(List<BancoInterCallbackPixDTO> bancoInterCallbackPixDTO) {
        for (BancoInterCallbackPixDTO dto : bancoInterCallbackPixDTO) {
            vendaService.confirmarPagamento(dto.txid(), StatusPagamento.APROVADO, StatusVenda.FINALIZADO, dto.horario().toLocalDateTime());
            Venda venda = vendaRepository.buscarPorVerificador(dto.txid()).orElseThrow(() -> new ExcecoesCustomizada("Venda não encontrada!", HttpStatus.NOT_FOUND));
            this.criarAssinatura(AssinaturaRequestDTO.builder()
                    .idVenda(venda.getId())
                    .build());
        }
    }

    @Override
    public void callbackBoleto(List<BancoInterCallbackBoletoDTO> bancoInterCallbackBoleto) {
        for (BancoInterCallbackBoletoDTO dto : bancoInterCallbackBoleto) {
            StatusPagamento statusPagamento = dto.situacao().equals("RECEBIDO") ? StatusPagamento.APROVADO : null;
            StatusVenda statusVenda = dto.situacao().equals("RECEBIDO") ? StatusVenda.FINALIZADO : null;
            LocalDateTime dataPagamento = dto.situacao().equals("RECEBIDO") ? dto.dataHoraSituacao().toLocalDateTime() : null;

            if (statusVenda == null || statusPagamento == null || dataPagamento == null) {
                return;
            }

            vendaService.confirmarPagamento(dto.codigoSolicitacao(), statusPagamento, statusVenda, dataPagamento);
            Venda venda = vendaRepository.buscarPorVerificador(dto.codigoSolicitacao()).orElseThrow(() -> new ExcecoesCustomizada("Venda não encontrada!", HttpStatus.NOT_FOUND));
            this.criarAssinatura(AssinaturaRequestDTO.builder()
                    .idVenda(venda.getId())
                    .build());
        }
    }

    @Override
    public Boolean callbackCartao(CieloResponse cardResponse) {
        if (cardResponse.pagamento().status().equals(2) && cardResponse.pagamento().mensagemRetorno().equals("Operation Successful")) {
            vendaService.confirmarPagamento(cardResponse.pagamento().idPagamento(), StatusPagamento.APROVADO, StatusVenda.FINALIZADO, LocalDateTime.parse(cardResponse.pagamento().dataCaptura().replaceAll(" ", "T")));

            this.criarAssinatura(AssinaturaRequestDTO.builder()
                    .idVenda(Long.valueOf(cardResponse.idPedidoLoja()))
                    .build());
            return true;
        } else {
            return false;
        }
    }
}
