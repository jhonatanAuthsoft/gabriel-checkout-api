package com.projeto.modelo.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.controller.dto.request.cielo.CieloReceberPagamentoCartao;
import com.projeto.modelo.controller.dto.response.cielo.CieloResponse;
import com.projeto.modelo.controller.dto.response.cielo.CieloResponseCallback;
import com.projeto.modelo.model.enums.BandeiraCartao;
import com.projeto.modelo.model.enums.TipoCartao;
import com.projeto.modelo.model.enums.TipoIdentificador;
import com.projeto.modelo.model.enums.TipoParcelamento;
import com.projeto.modelo.service.CieloService;
import com.projeto.modelo.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
public class CieloServiceImp implements CieloService {


    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ObjectMapper mapper;

    @Value("${cielo.merchant-id}")
    private String merchantId;

    @Value("${cielo.merchant-key}")
    private String merchantKey;

    @Value("${cielo.url-base}")
    private String baseUrl;

    @Value("${cielo.url-base-query}")
    private String baseUrlQuery;

    @Override
    public CieloResponse pagarCielo(CieloReceberPagamentoCartao req) {
        try {
            ObjectNode paymentBody = montarJsonPagamento(req);
            String json = paymentBody.toString();

            log.info("JSON: {}", json);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/1/sales/"))
                    .header("Content-Type", "application/json")
                    .header("MerchantId", merchantId)
                    .header("MerchantKey", merchantKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                return mapper.readValue(
                        resp.body(),
                        CieloResponse.class
                );
            } else {
                throw new RuntimeException("Erro ao criar pagamento: " + resp.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha na requisição Cielo", e);
        }
    }

    @Override
    public CieloResponseCallback consultarTransacaoPorPaymentId(String paymentId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrlQuery + "/1/sales/" + paymentId))
                    .header("MerchantId", merchantId)
                    .header("MerchantKey", merchantKey)
                    .GET()
                    .build();

            HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() / 100 == 2) {
                return mapper.readValue(
                        resp.body(),
                        CieloResponseCallback.class
                );
            } else {
                throw new RuntimeException("Erro ao consultar transação: " + resp.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao consultar transação por PaymentId", e);
        }
    }

    private String determinarTipoIdentificador(String identificador) {
        if (StringUtils.isNullOrEmpty(identificador)) {
            throw new ExcecoesCustomizada("Identificador não pode ficar em branco!", HttpStatus.BAD_REQUEST);
        }

        String valor = identificador.replaceAll("\\D", "");

        if (valor.matches("\\d{11}")) {
            return TipoIdentificador.CPF.toString();
        } else if (valor.matches("\\d{14}")) {
            return TipoIdentificador.CNPJ.toString();
        } else if (valor.matches("[A-Za-z0-9]{5,20}")) {
            return TipoIdentificador.RG.toString();
        } else {
            throw new ExcecoesCustomizada("Identificador não pode encontrado", HttpStatus.BAD_REQUEST);
        }
    }

    private String resolverNomePropriedade(TipoCartao tipoCartao) {
        if (tipoCartao == null) {
            throw new ExcecoesCustomizada("Identificador não pode ficar em branco!", HttpStatus.BAD_REQUEST);
        }

        return switch (tipoCartao) {
            case CREDITO -> "CreditCard";
            case DEBITO -> "DebitCard";
        };
    }

    private String resolverBandeiraCartao(BandeiraCartao bandeiraCartao) {
        if (bandeiraCartao == null) {
            throw new ExcecoesCustomizada("bandeira do cartao não pode ficar em branco!", HttpStatus.BAD_REQUEST);
        }

        return switch (bandeiraCartao) {
            case VISA -> "Visa";
            case MASTER -> "Master";
            case AMEX -> "Amex";
            case ELO -> "Elo";
            case AURA -> "Aura";
            case JCB -> "JCB";
            case DINERS -> "Diners";
            case DISCOVER -> "Discover";
        };

    }

    private String determinarPagadorDeJuros(TipoParcelamento tipoParcelamento) {
        if (tipoParcelamento == null) {
            throw new ExcecoesCustomizada("tipo parcelamento do cartao não pode ficar em branco!", HttpStatus.BAD_REQUEST);
        }

        return switch (tipoParcelamento) {
            case LOJISTA -> "ByMerchant";
            case COMPRADOR -> "ByIssuer";
        };
    }

    private ObjectNode montarJsonPagamento(CieloReceberPagamentoCartao req) {
        ObjectNode root = mapper.createObjectNode();
        root.put("MerchantOrderId", req.merchantOrderId());

        ObjectNode customer = root.putObject("Customer");
        customer.put("Name", req.proprietario().nome());
        customer.put("Identity", req.proprietario().cpfCnpjRg());
        customer.put("IdentityType", determinarTipoIdentificador(req.proprietario().cpfCnpjRg()));
        customer.put("Email", req.proprietario().email());
        customer.put("Birthdate", req.proprietario().aniversario());
        customer.set("Address", mapper.valueToTree(req.proprietario().enderecoProprietario()));
        customer.set("DeliveryAddress", mapper.valueToTree(req.proprietario().enderecoEntrega()));
        customer.set("Billing", mapper.valueToTree(req.proprietario().enderecoCobranca()));

        String nomePropriedadeCartao = resolverNomePropriedade(req.pagamento().tipoCartao());

        ObjectNode cartao = root.putObject(nomePropriedadeCartao);

        cartao.put("CardNumber", req.pagamento().cieloCartao().numeroCartao());
        cartao.put("Holder", req.pagamento().cieloCartao().nomeImpresso());
        cartao.put("ExpirationDate", req.pagamento().cieloCartao().dataVencimento());
        cartao.put("SecurityCode", req.pagamento().cieloCartao().codigoSeguranca());
        cartao.put("Brand", resolverBandeiraCartao(req.pagamento().cieloCartao().bandeiraCartao()));

        ObjectNode payment = root.putObject("Payment");
        payment.put("Type", nomePropriedadeCartao);
        payment.put("Amount", req.pagamento().valor().multiply(BigDecimal.valueOf(100)).intValue());
        payment.put("Capture", req.pagamento().capture());
        payment.put("Currency", req.pagamento().moeda());
        payment.put("Country", req.pagamento().pais());
        payment.put("SoftDescriptor", req.pagamento().softDescriptor());
        payment.put("Installments", req.pagamento().parcelas());
        payment.put("Interest", determinarPagadorDeJuros(req.pagamento().tipoParcelamento()));
        payment.put("Capture", req.pagamento().capture());
        payment.set(nomePropriedadeCartao, cartao);

        return root;
    }

}
