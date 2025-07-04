package com.projeto.modelo.service.imp;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.modelo.configuracao.exeption.ExcecoesCustomizada;
import com.projeto.modelo.controller.dto.request.bancoInter.boleto.BancoInterBoletoRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.boleto.BancoInterCallbackBoletoDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.boleto.BancoInterPagadorBoletoRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.pix.BancoInterPixRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.pix.BancoInterWebhookRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.pix.calback.BancoInterCallbackPixDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.BancoInterAccessTokenResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.BancoInterWebhookResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterBoletoPDFResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterBoletoResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterCodigoBoletoResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.pix.BancoInterPixResponseDTO;
import com.projeto.modelo.service.BancoInterService;
import com.projeto.modelo.util.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.time.Instant;
import java.util.*;

@Service
@Log4j2
public class BancoInterServiceImp implements BancoInterService {

    @Value("${inter.url.raiz}")
    private String urlRaiz;

    @Value("${inter.clientSecret}")
    private String clientSecret;

    @Value("${inter.clientId}")
    private String clientId;

    @Value("${inter.arquivo.keyboard.senha}")
    private String keyboardSenha;

    @Value("${inter.arquivo.truststore.senha}")
    private String truststoreSenha;

    @Value("${inter.arquivo.keyboard}")
    private String keyboard;

    @Value("${inter.arquivo.truststore}")
    private String truststore;

    @Value("${inter.chave.pix}")
    private String chavePix;

    private String AUTENTICACAO = "/oauth/v2/token";
    private String PIX_COB_URL = "/pix/v2/cob";
    private String WEBHOOK_PIX = "/pix/v2/webhook";
    private String WEBHOOK_BOLETO = "/cobranca/v3/cobrancas/webhook";
    private String GERA_BOLETO_URL = "/cobranca/v3/cobrancas";
    private String CERTIFICADO = "certificado";

    private BancoInterAccessTokenResponseDTO bancoInterAccessToken = new BancoInterAccessTokenResponseDTO();


    @Override
    public BancoInterPixResponseDTO geraPix(BancoInterPixRequestDTO bancoInterPixRequestDTO) {
        try {

            // Autentica e obtém o token de acesso se necessário
            if (Objects.isNull(this.bancoInterAccessToken) || Objects.isNull(this.bancoInterAccessToken.getHorarioExpiraToken())
                || Instant.now().isAfter(this.bancoInterAccessToken.getHorarioExpiraToken())) {
                this.autenticar();
            }

            // Cria o cliente HTTP com mTLS configurado
            HttpClient client = this.httpComSSLConfigurado();

            Map<String, Object> payloadGeraPix = this.payloadGeraPix(bancoInterPixRequestDTO);

            // Converte o payload para JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(payloadGeraPix);

            // Monta a requisição HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.urlRaiz + this.PIX_COB_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.bancoInterAccessToken.getAccessToken())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Envia a requisição e obtém a resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            this.validaRetornoInter(response);
            BancoInterPixResponseDTO bancoInterPixResponse = objectMapper.readValue(response.body(), BancoInterPixResponseDTO.class);

            return bancoInterPixResponse;
        } catch (Exception e) {
            throw new ExcecoesCustomizada(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }


    @Override
    public BancoInterPixResponseDTO consultaPix(String txid) {
        try {
            if (Objects.isNull(this.bancoInterAccessToken) || Objects.isNull(this.bancoInterAccessToken.getHorarioExpiraToken())
                || Instant.now().isAfter(this.bancoInterAccessToken.getHorarioExpiraToken())) {
                this.autenticar();
            }

            HttpClient client = this.httpComSSLConfigurado();
            String url = this.urlRaiz + this.PIX_COB_URL + "/" + txid;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.bancoInterAccessToken.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            this.validaRetornoInter(response);
            ObjectMapper objectMapper = new ObjectMapper();
            BancoInterPixResponseDTO bancoInterPixResponse = objectMapper.readValue(response.body(), BancoInterPixResponseDTO.class);

            return bancoInterPixResponse;

        } catch (Exception e) {
            throw new ExcecoesCustomizada(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    @Override
    public void cadastraWebhookPix(BancoInterWebhookRequestDTO webhookPixRequest) {
        try {

            // Autentica e obtém o token de acesso se necessário
            if (Objects.isNull(this.bancoInterAccessToken) || Objects.isNull(this.bancoInterAccessToken.getHorarioExpiraToken())
                || Instant.now().isAfter(this.bancoInterAccessToken.getHorarioExpiraToken())) {
                this.autenticar();
            }

            // Cria o cliente HTTP com mTLS configurado
            HttpClient client = this.httpComSSLConfigurado();

            Map<String, Object> payloadGeraPix = this.payloadWebhookPix(webhookPixRequest.webhookUrl());

            // Converte o payload para JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(payloadGeraPix);

            // Monta a requisição HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.urlRaiz + this.WEBHOOK_PIX + "/" + this.chavePix))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.bancoInterAccessToken.getAccessToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Envia a requisição e obtém a resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            this.validaRetornoInter(response);
        } catch (Exception e) {
            throw new ExcecoesCustomizada(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    @Override
    public BancoInterWebhookResponseDTO buscaWebhookPix() {
        try {
            if (Objects.isNull(this.bancoInterAccessToken) || Objects.isNull(this.bancoInterAccessToken.getHorarioExpiraToken())
                || Instant.now().isAfter(this.bancoInterAccessToken.getHorarioExpiraToken())) {
                this.autenticar();
            }

            HttpClient client = this.httpComSSLConfigurado();
            String url = this.urlRaiz + this.WEBHOOK_PIX + "/" + this.chavePix;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.bancoInterAccessToken.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            this.validaRetornoInter(response);
            ObjectMapper objectMapper = new ObjectMapper();
            BancoInterWebhookResponseDTO webhookPixResponse = objectMapper.readValue(response.body(), BancoInterWebhookResponseDTO.class);

            return webhookPixResponse;

        } catch (Exception e) {
            throw new ExcecoesCustomizada(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    @Override
    public void deletarWebhookPix() {
        try {
            if (Objects.isNull(this.bancoInterAccessToken) || Objects.isNull(this.bancoInterAccessToken.getHorarioExpiraToken())
                || Instant.now().isAfter(this.bancoInterAccessToken.getHorarioExpiraToken())) {
                this.autenticar();
            }

            HttpClient client = this.httpComSSLConfigurado();
            String url = this.urlRaiz + this.WEBHOOK_PIX + "/" + this.chavePix;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.bancoInterAccessToken.getAccessToken())
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            this.validaRetornoInter(response);
        } catch (Exception e) {
            throw new ExcecoesCustomizada(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    @Override
    public void callbackPix(List<BancoInterCallbackPixDTO> bancoInterCallbackPixDTO) {
        // TODO validação no recebimento do pix
        try {

        } catch (Exception e) {
            log.error("Erro Callback PIX: Objeto recebido: {}", bancoInterCallbackPixDTO);
            throw new ExcecoesCustomizada("Erro Callback PIX", HttpStatus.BAD_REQUEST);
        }
    }


    private Map<String, Object> payloadWebhookPix(String urlWebhook) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("webhookUrl", urlWebhook);
        return payload;
    }

    private Map<String, Object> payloadGeraPix(BancoInterPixRequestDTO bancoInterPixRequestDTO) {

        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> calendario = new HashMap<>();
        Map<String, Object> valor = new HashMap<>();


        if (Objects.nonNull(bancoInterPixRequestDTO.expiracao())) {
            calendario.put("expiracao", bancoInterPixRequestDTO.expiracao()); // Tempo de vida da cobrança, especificado em segundos a partir da data de criação
            payload.put("calendario", calendario);
        } else {
            calendario.put("expiracao", 86400); // Tempo de vida da cobrança, especificado em segundos a partir da data de criação
            payload.put("calendario", calendario);
        }

        /*
        devedor
        Os campos aninhados sob o objeto devedor são opcionais e identificam a pessoa ou a instituição a quem a
        cobrança está endereçada. Não identifica, necessariamente, quem irá efetivamente realizar o pagamento.

        Não é permitido que o campo devedor.cpf e campo devedor.cnpj estejam preenchidos ao mesmo tempo. Se o campo
        devedor.nome está preenchido, então deve existir ou um devedor.cpf ou um campo devedor.cnpj preenchido.
         */

        if (Objects.nonNull(bancoInterPixRequestDTO.devedorNome()) && Objects.nonNull(bancoInterPixRequestDTO.devedorCpfCnpj())) {
            Map<String, Object> devedor = new HashMap<>();
            if (bancoInterPixRequestDTO.devedorCpfCnpj().length() == 11) {
                devedor.put("cpf", bancoInterPixRequestDTO.devedorCpfCnpj());
            } else {
                devedor.put("cnpj", bancoInterPixRequestDTO.devedorCpfCnpj());
            }
            devedor.put("nome", bancoInterPixRequestDTO.devedorNome());
            payload.put("devedor", devedor);
        }

        BigDecimal valorOriginal = bancoInterPixRequestDTO.valorPagamento().setScale(2, RoundingMode.HALF_UP);
        valor.put("original", valorOriginal.toString()); // Valor original da cobrança. formato \d{1,10}\.\d{2}
        /*
        modalidadeAlteracao
        Trata-se de um campo que determina se o valor final do documento pode ser alterado pelo pagador.

        Na ausência desse campo, assume-se que não se pode alterar o valor do documento de cobrança, ou seja,
        assume-se o valor 0. Se o campo estiver presente e com valor 1, então está determinado que o valor final da
        cobrança pode ter seu valor alterado pelo pagador.
         */
        valor.put("modalidadeAlteracao", 0);// TODO alterar conforme necessidade
        payload.put("valor", valor);

        // Chave pix para recebimento, Os tipos de chave podem ser: telefone, e-mail, cpf/cnpj ou EVP
        payload.put("chave", this.chavePix);

        /* opcional, determina um texto a ser apresentado ao pagador para que ele possa digitar uma informação correlata, em formato livre
        LIMITE de 140 caracteres
         */
        payload.put("solicitacaoPagador", "TEXTO PARA O CLIENTE"); // TODO alterar conforme necessidade
        return payload;
    }

    private void validaRetornoInter(HttpResponse<String> response) {
        // Processa a resposta
        if (response.statusCode() == 400) {
            log.error("Erro PIX: 400 - Formato invalido mensagem: {}", response.body());
            throw new ExcecoesCustomizada("Erro: contate o suporte", HttpStatus.BAD_REQUEST);
        }

        if (response.statusCode() == 403) {
            log.error("Erro PIX: 403 - Autenticação com problema: {}", response.body());
            throw new ExcecoesCustomizada("Erro: contate o suporte", HttpStatus.BAD_REQUEST);
        }

        if (response.statusCode() == 404) {
            log.error("Erro PIX: 404 - Recurso não localizado no banco: {}", response.body());
            throw new ExcecoesCustomizada("Erro: Recurso não localizado, verifique os dados informados", HttpStatus.BAD_REQUEST);
        }

        if (response.statusCode() == 503) {
            log.error("Erro PIX: 503 - Banco inter fora do ar: {}", response.body());
            throw new ExcecoesCustomizada("Erro: O banco esta fora, por favor tente mais tarde", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (response.statusCode() != 201 && response.statusCode() != 200 && response.statusCode() != 204) {
            log.error("Erro PIX: {} - Desconecido: {}", response.statusCode(), response.body());
            throw new ExcecoesCustomizada("Erro: O banco esta fora, por favor tente mais tarde", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public BancoInterCodigoBoletoResponseDTO gerarBoleto(BancoInterBoletoRequestDTO bancoInterBoletoRequestDTO) {
        try {

            if (Objects.isNull(this.bancoInterAccessToken) || Objects.isNull(this.bancoInterAccessToken.getHorarioExpiraToken())
                || Instant.now().isAfter(this.bancoInterAccessToken.getHorarioExpiraToken())) {
                this.autenticar();
            }

            // Cria o cliente HTTP com mTLS configurado
            HttpClient client = this.httpComSSLConfigurado();
            Map<String, Object> payloadedGeraBoleto = this.payloadGeraBoleto(bancoInterBoletoRequestDTO);

            // Converte o payload para JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(payloadedGeraBoleto);

            // Monta a requisição HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.urlRaiz + this.GERA_BOLETO_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.bancoInterAccessToken.getAccessToken())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Envia a requisição e obtém a resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            this.validaRetornoInter(response);
            BancoInterCodigoBoletoResponseDTO boletoResponse = objectMapper.readValue(response.body(), BancoInterCodigoBoletoResponseDTO.class);

            return boletoResponse;
        } catch (Exception e) {
            throw new ExcecoesCustomizada(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    private Map<String, Object> payloadGeraBoleto(BancoInterBoletoRequestDTO bancoInterBoletoRequestDTO) {
        BancoInterPagadorBoletoRequestDTO PagadorBoletoRequest = bancoInterBoletoRequestDTO.pagador();

        // Monta o payload como JSON
        Map<String, Object> payload = new HashMap<>();

        payload.put("seuNumero", "1"); // TODO Campo Seu Número do título
        payload.put("valorNominal", bancoInterBoletoRequestDTO.valorPagamento()); // TODO valor do titulo
        payload.put("dataVencimento", bancoInterBoletoRequestDTO.dataVencimento().toString()); // TODO data de vencimento do titulo

        // Número de dias corridos após o vencimento para o cancelamento efetivo automático da cobrança. (de 0 até 60)
        payload.put("numDiasAgenda", "0"); // TODO verificar configuração do sistema

        Map<String, Object> pagador = new HashMap<>();
        pagador.put("cpfCnpj", PagadorBoletoRequest.cpfCnpj());

        if (PagadorBoletoRequest.cpfCnpj().length() == 11) {
            pagador.put("tipoPessoa", "FISICA");
        } else {
            pagador.put("tipoPessoa", "JURIDICA");
        }

        pagador.put("nome", PagadorBoletoRequest.nome());
        pagador.put("endereco", PagadorBoletoRequest.endereco());
        pagador.put("cidade", PagadorBoletoRequest.cidade());
        pagador.put("uf", PagadorBoletoRequest.uf().toString());
        pagador.put("cep", StringUtils.removerCaracteresEspeciais(PagadorBoletoRequest.cep()));
        payload.put("pagador", pagador);

        // Caso queria colocar mensagem no cortpo do email
        Map<String, String> mensagem = new HashMap<>();
        mensagem.put("linha1", "Compra de Nº ");
        mensagem.put("linha2", "Caso o boleto não seja pago até a data");
        mensagem.put("linha3", "a compra será cancelada Fique atendo nos vencimentos e descontos");
        mensagem.put("linha4", "Dúvidas, fale conosco.");
        payload.put("mensagem", mensagem);

        return payload;
    }

    @Override
    public BancoInterBoletoResponseDTO consultaBoleto(String codigoBoleto) {
        try {
            if (Objects.isNull(this.bancoInterAccessToken) || Objects.isNull(this.bancoInterAccessToken.getHorarioExpiraToken())
                || Instant.now().isAfter(this.bancoInterAccessToken.getHorarioExpiraToken())) {
                this.autenticar();
            }

            HttpClient client = this.httpComSSLConfigurado();

            String url = this.urlRaiz + this.GERA_BOLETO_URL + "/" + codigoBoleto;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.bancoInterAccessToken.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            this.validaRetornoInter(response);
            ObjectMapper objectMapper = new ObjectMapper();
            BancoInterBoletoResponseDTO boletoResponseDTO = objectMapper.readValue(response.body(), BancoInterBoletoResponseDTO.class);
            return boletoResponseDTO;

        } catch (Exception e) {
            throw new ExcecoesCustomizada(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    @Override
    public BancoInterBoletoPDFResponseDTO consultarBoletoPdf(String codigoBoleto) {
        try {
            if (Objects.isNull(this.bancoInterAccessToken) || Objects.isNull(this.bancoInterAccessToken.getHorarioExpiraToken())
                || Instant.now().isAfter(this.bancoInterAccessToken.getHorarioExpiraToken())) {
                this.autenticar();
            }

            HttpClient client = this.httpComSSLConfigurado();

            String url = this.urlRaiz + this.GERA_BOLETO_URL + "/" + codigoBoleto + "/pdf";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.bancoInterAccessToken.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            this.validaRetornoInter(response);
            ObjectMapper objectMapper = new ObjectMapper();
            BancoInterBoletoPDFResponseDTO boletoPDFResponse = objectMapper.readValue(response.body(), BancoInterBoletoPDFResponseDTO.class);
            return boletoPDFResponse;

        } catch (Exception e) {
            throw new ExcecoesCustomizada(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }


    @Override
    public void cadastraWebhookBoleto(BancoInterWebhookRequestDTO webhookPixRequest) {
        try {

            // Autentica e obtém o token de acesso se necessário
            if (Objects.isNull(this.bancoInterAccessToken) || Objects.isNull(this.bancoInterAccessToken.getHorarioExpiraToken())
                || Instant.now().isAfter(this.bancoInterAccessToken.getHorarioExpiraToken())) {
                this.autenticar();
            }

            // Cria o cliente HTTP com mTLS configurado
            HttpClient client = this.httpComSSLConfigurado();

            Map<String, Object> payloadGeraPix = this.payloadWebhookPix(webhookPixRequest.webhookUrl());

            // Converte o payload para JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(payloadGeraPix);

            // Monta a requisição HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.urlRaiz + this.WEBHOOK_BOLETO))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.bancoInterAccessToken.getAccessToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Envia a requisição e obtém a resposta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            this.validaRetornoInter(response);
        } catch (Exception e) {
            throw new ExcecoesCustomizada(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    @Override
    public BancoInterWebhookResponseDTO buscaWebhookBoleto() {
        try {
            if (Objects.isNull(this.bancoInterAccessToken) || Objects.isNull(this.bancoInterAccessToken.getHorarioExpiraToken())
                || Instant.now().isAfter(this.bancoInterAccessToken.getHorarioExpiraToken())) {
                this.autenticar();
            }

            HttpClient client = this.httpComSSLConfigurado();
            String url = this.urlRaiz + this.WEBHOOK_BOLETO;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.bancoInterAccessToken.getAccessToken())
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            this.validaRetornoInter(response);
            ObjectMapper objectMapper = new ObjectMapper();
            BancoInterWebhookResponseDTO webhookPixResponse = objectMapper.readValue(response.body(), BancoInterWebhookResponseDTO.class);

            return webhookPixResponse;

        } catch (Exception e) {
            throw new ExcecoesCustomizada(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    @Override
    public void deletarWebhookBoleto() {
        try {
            if (Objects.isNull(this.bancoInterAccessToken) || Objects.isNull(this.bancoInterAccessToken.getHorarioExpiraToken())
                || Instant.now().isAfter(this.bancoInterAccessToken.getHorarioExpiraToken())) {
                this.autenticar();
            }

            HttpClient client = this.httpComSSLConfigurado();
            String url = this.urlRaiz + this.WEBHOOK_BOLETO;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.bancoInterAccessToken.getAccessToken())
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            this.validaRetornoInter(response);
        } catch (Exception e) {
            throw new ExcecoesCustomizada(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    @Override
    public void callbackBoleto(List<BancoInterCallbackBoletoDTO> bancoInterCallbackBoletoDTO) {
        // TODO validação recebimento BOLETO validar cobranças pagas, canceladas e expiradas
        try {

        } catch (Exception e) {
            log.error("Erro Callback BOLETO: Objeto recebido: {}", bancoInterCallbackBoletoDTO);
            throw new ExcecoesCustomizada("Erro Callback BOLETO", HttpStatus.BAD_REQUEST);
        }
    }

    private void autenticar() {
        try {
            // URL do endpoint
            String url = urlRaiz + AUTENTICACAO;
            // Criar o cliente HTTP com o contexto SSL configurado
            HttpClient client = this.httpComSSLConfigurado();
            String credentials = clientId + ":" + clientSecret;
            // Headers
            String authorization = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
            ;

            // Corpo da requisição (form-urlencoded)
            String body = "grant_type=" + URLEncoder.encode("client_credentials", StandardCharsets.UTF_8) +
                          "&scope=" + URLEncoder.encode("cob.write cob.read cobv.write cobv.read lotecobv.write " +
                                                        "lotecobv.read pix.write pix.read webhook.write webhook.read payloadlocation.write " +
                                                        "payloadlocation.read boleto-cobranca.read boleto-cobranca.write extrato.read pagamento-pix.write " +
                                                        "pagamento-pix.read extrato-usend.read pagamento-boleto.read pagamento-boleto.write pagamento-darf.write " +
                                                        "pagamento-lote.write pagamento-lote.read webhook-banking.read webhook-banking.write", StandardCharsets.UTF_8);

            // Criar requisição
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", authorization)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    //.header("Cookie", cookie)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            // Enviar requisição
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == HttpStatus.OK.value()) {
                // Mapeando o JSON para o objeto
                ObjectMapper objectMapper = new ObjectMapper();
                BancoInterAccessTokenResponseDTO bancoInterAccessToken = objectMapper.readValue(response.body(), BancoInterAccessTokenResponseDTO.class);
                Instant horarioToken = Instant.now();
                bancoInterAccessToken.setHorarioExpiraToken(horarioToken.plusSeconds(bancoInterAccessToken.getExpiresIn()));
                this.bancoInterAccessToken = bancoInterAccessToken;
            } else if (response.statusCode() >= 400 && response.statusCode() < 500) {
                throw new ExcecoesCustomizada("Erro interno, tente novamente", HttpStatus.valueOf(response.statusCode()));
            } else if (response.statusCode() >= 500 && response.statusCode() < 600) {
                throw new ExcecoesCustomizada("Erro no banco, favor tentar mais tarde", HttpStatus.valueOf(response.statusCode()));
            } else {
                throw new ExcecoesCustomizada("Erro no banco, favor tentar mais tarde", HttpStatus.BAD_GATEWAY);
            }

        } catch (Exception e) {
            throw new ExcecoesCustomizada(e.getMessage() + " - AUT-I", HttpStatus.BAD_GATEWAY);
        }
    }

    private HttpClient httpComSSLConfigurado() {
        try {
            char[] keystorePassword = this.keyboardSenha.toCharArray();
            char[] truststorePassword = this.truststoreSenha.toCharArray();

            // Remove "classpath:" se estiver presente
            String keystorePath = this.keyboard.replace("classpath:", "");
            String truststorePath = this.truststore.replace("classpath:", "");

            // Carregar o keystore (para autenticação do cliente)
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream keystoreStream = getClass().getClassLoader().getResourceAsStream(keystorePath)) {
                if (keystoreStream == null) {
                    throw new FileNotFoundException("Keystore file not found in resources");
                }
                keyStore.load(keystoreStream, keystorePassword);
            }

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keystorePassword);

            // Carregar o truststore (para validação do certificado do servidor)
            KeyStore trustStore = KeyStore.getInstance("JKS");
            try (InputStream truststoreStream = getClass().getClassLoader().getResourceAsStream(truststorePath)) {
                if (truststoreStream == null) {
                    throw new FileNotFoundException("Truststore file not found in resources");
                }
                trustStore.load(truststoreStream, truststorePassword);
            }

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            log.info("[DEBUG SSL] Iniciando configuração do SSLContext");
            log.info("[DEBUG SSL] Keystore path: {}", keystorePath);
            log.info("[DEBUG SSL] Truststore path: {}", truststorePath);
            log.info("[DEBUG SSL] Keystore password length: {}", keystorePassword.length);
            log.info("[DEBUG SSL] Truststore password length: {}", truststorePassword.length);
            log.info("[DEBUG SSL] Keystore aliases:");
            Enumeration<String> keystoreAliases = keyStore.aliases();
            while (keystoreAliases.hasMoreElements()) {
                log.info("[DEBUG SSL]   - {}", keystoreAliases.nextElement());
            }
            log.info("[DEBUG SSL] Truststore aliases:");
            Enumeration<String> truststoreAliases = trustStore.aliases();
            while (truststoreAliases.hasMoreElements()) {
                log.info("[DEBUG SSL]   - {}", truststoreAliases.nextElement());
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
            log.info("[DEBUG SSL] SSLContext inicializado com sucesso");

            return HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .build();

        } catch (Exception e) {
            throw new ExcecoesCustomizada("httpComSSLConfigurado - Erro no banco, favor tentar mais tarde: " + e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }


}
