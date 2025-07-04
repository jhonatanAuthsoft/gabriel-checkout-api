//package com.projeto.modelo.controller;
//
//
//import com.projeto.modelo.controller.dto.request.bancoInter.boleto.BancoInterBoletoRequestDTO;
//import com.projeto.modelo.controller.dto.request.bancoInter.boleto.BancoInterCallbackBoletoDTO;
//import com.projeto.modelo.controller.dto.request.bancoInter.pix.BancoInterPixRequestDTO;
//import com.projeto.modelo.controller.dto.request.bancoInter.pix.BancoInterWebhookRequestDTO;
//import com.projeto.modelo.controller.dto.request.bancoInter.pix.calback.BancoInterCallbackPixDTO;
//import com.projeto.modelo.controller.dto.response.bancoInter.BancoInterWebhookResponseDTO;
//import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterBoletoPDFResponseDTO;
//import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterBoletoResponseDTO;
//import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterCodigoBoletoResponseDTO;
//import com.projeto.modelo.controller.dto.response.bancoInter.pix.BancoInterPixResponseDTO;
//import com.projeto.modelo.service.BancoInterService;
//import java.util.List;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping(value = "/inter")
//public class BancoInterController {
//
//    private final BancoInterService bancoInterService;
//
//    public BancoInterController(BancoInterService bancoInterService) {
//        this.bancoInterService = bancoInterService;
//    }
//
//     /*
//     TODO Todas as APIs
//      https://developers.inter.co/references/token
//      * Autenticacao = IMPLEMENTADO
//      * API Cobranca (Boleto com Pix) = IMPLEMENTADO PARCIAL
//      * API Banking = NAO IMPLEMENTADO
//      * API PIX = IMPLEMENTADO PARCIAL
//      * API PIX automatico = NAO IMPLEMENTADO
//     */
//
//
//    @PostMapping("/gera-pix")
//    public ResponseEntity<BancoInterPixResponseDTO> geraPix(@RequestBody BancoInterPixRequestDTO bancoInterPixRequestDTO) {
//        BancoInterPixResponseDTO bancoInterPixResponse = this.bancoInterService.geraPix(bancoInterPixRequestDTO);
//        return new ResponseEntity<>(bancoInterPixResponse, HttpStatus.OK);
//    }
//
//    @GetMapping("/consulta-pix")
//    public ResponseEntity<BancoInterPixResponseDTO> consultaPix(@RequestParam(value = "txid", required = true) String txid) {
//        BancoInterPixResponseDTO bancoInterPixResponse = this.bancoInterService.consultaPix(txid);
//        return new ResponseEntity<>(bancoInterPixResponse, HttpStatus.OK);
//    }
//
//    @PutMapping("/cadastrar-webhook-pix")
//    public ResponseEntity<Void> cadastraWebhookPix(@RequestBody BancoInterWebhookRequestDTO webhookPixRequest) {
//        this.bancoInterService.cadastraWebhookPix(webhookPixRequest);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @GetMapping("/buscar-webhook-pix")
//    public ResponseEntity<BancoInterWebhookResponseDTO> buscaWebhookPix() {
//        BancoInterWebhookResponseDTO webhookPixResponse = this.bancoInterService.buscaWebhookPix();
//        return new ResponseEntity<>(webhookPixResponse, HttpStatus.OK);
//    }
//
//    @DeleteMapping("/deletar-webhook-pix")
//    public ResponseEntity<Void> deletarWebhookPix() {
//        this.bancoInterService.deletarWebhookPix();
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    /*
//        Metodo que recebe o callback de pagamento, ou seja se venho neste metodo o pix foi pago.
//        Método destinado a criar um webhook para receber notificações (callbacks) relacionados a
//        Pix Cobrança (recebimento do valor cobrado).
//
//        Caso o sistema nao retone 200 OK no recebimento do callback, serão realizadas até 4 tentativas com
//        intervalos de 20, 30, 60 e 120 minutos.
//     */
//    @PostMapping("/callback/pix")
//    public ResponseEntity<Void> callbackPix(@RequestBody List<BancoInterCallbackPixDTO> bancoInterCallbackPixDTO) {
//        this.bancoInterService.callbackPix(bancoInterCallbackPixDTO);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @PostMapping("/gerar-boleto")
//    public ResponseEntity<BancoInterCodigoBoletoResponseDTO> pagamentoBoleto(@RequestBody BancoInterBoletoRequestDTO bancoInterBoletoRequestDTO) {
//        BancoInterCodigoBoletoResponseDTO bancoInterBoletoResponseDTO = this.bancoInterService.gerarBoleto(bancoInterBoletoRequestDTO);
//        return new ResponseEntity<>(bancoInterBoletoResponseDTO, HttpStatus.OK);
//    }
//
//    @GetMapping("/consultar-boleto")
//    public ResponseEntity<BancoInterBoletoResponseDTO> consultarBoleto(@RequestParam(value = "codigoSolicitacao", required = true) String codigoSolicitacao) {
//        BancoInterBoletoResponseDTO boletoResponseDTO = this.bancoInterService.consultaBoleto(codigoSolicitacao);
//        return new ResponseEntity(boletoResponseDTO, HttpStatus.OK);
//    }
//
//    @GetMapping("/consultar-boleto-pdf")
//    public ResponseEntity<BancoInterBoletoPDFResponseDTO> consultarBoletoPdf(@RequestParam(value = "codigoSolicitacao", required = true) String codigoSolicitacao) {
//        BancoInterBoletoPDFResponseDTO boletoPDFResponse = this.bancoInterService.consultarBoletoPdf(codigoSolicitacao);
//        return new ResponseEntity(boletoPDFResponse, HttpStatus.OK);
//    }
//
//    @PutMapping("/cadastrar-webhook-boleto")
//    public ResponseEntity<Void> cadastraWebhookBoleto(@RequestBody BancoInterWebhookRequestDTO webhookPixRequest) {
//        this.bancoInterService.cadastraWebhookBoleto(webhookPixRequest);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @GetMapping("/buscar-webhook-boleto")
//    public ResponseEntity<BancoInterWebhookResponseDTO> buscaWebhookBoleto() {
//        BancoInterWebhookResponseDTO webhookPixResponse = this.bancoInterService.buscaWebhookBoleto();
//        return new ResponseEntity<>(webhookPixResponse, HttpStatus.OK);
//    }
//
//    @DeleteMapping("/deletar-webhook-boleto")
//    public ResponseEntity<Void> deletarWebhookBoleto() {
//        this.bancoInterService.deletarWebhookBoleto();
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    /*
//    Método destinado a criar um webhook para receber notificações (callbacks) de cobranças pagas, canceladas e expiradas.
//
//    Caso o servidor de webhook retorne erro de recebimento do callback, serão realizadas até 4 tentativas
//    com intervalos de 20, 30, 60 e 120 minutos.
//     */
//    @PostMapping("/callback/boleto")
//    public ResponseEntity<Void> callbackBoleto(@RequestBody List<BancoInterCallbackBoletoDTO> bancoInterCallbackBoleto) {
//        this.bancoInterService.callbackBoleto(bancoInterCallbackBoleto);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//}