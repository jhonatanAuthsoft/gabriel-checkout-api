package com.projeto.modelo.controller;

import com.projeto.modelo.controller.dto.request.PagamentoRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.boleto.BancoInterCallbackBoletoDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.pix.calback.BancoInterCallbackPix;
import com.projeto.modelo.controller.dto.request.bancoInter.pix.calback.BancoInterCallbackPixDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterBoletoPDFResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.pix.BancoInterPixResponseDTO;
import com.projeto.modelo.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamento")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping("/pix")
    public ResponseEntity<BancoInterPixResponseDTO> pagarPix(@RequestBody PagamentoRequestDTO pagamentoRequestDTO) {
        return new ResponseEntity<>(this.pagamentoService.pagarComPix(pagamentoRequestDTO), HttpStatus.OK);
    }

    @PostMapping("/boleto")
    public ResponseEntity<BancoInterBoletoPDFResponseDTO> pagarBoleto(@RequestBody PagamentoRequestDTO pagamentoRequestDTO) throws InterruptedException {
        return new ResponseEntity<>(this.pagamentoService.pagarComBoleto(pagamentoRequestDTO), HttpStatus.OK);
    }

    @PostMapping("/buscar-boleto/{codigoSolicitacao}")
    public ResponseEntity<BancoInterBoletoPDFResponseDTO> pagarBoleto(@PathVariable String codigoSolicitacao) {
        return new ResponseEntity<>(this.pagamentoService.buscarBoleto(codigoSolicitacao), HttpStatus.OK);
    }

    @PostMapping("/cartao")
    public ResponseEntity<Boolean> pagarCartao(@RequestBody PagamentoRequestDTO pagamentoRequestDTO) {
        return new ResponseEntity<>(this.pagamentoService.pagarComCartao(pagamentoRequestDTO), HttpStatus.OK);
    }

    @PostMapping("/callback/pix")
    public ResponseEntity<Void> callbackPix(@RequestBody BancoInterCallbackPix payload) {
        this.pagamentoService.callbackPix(payload);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/callback/boleto")
    public ResponseEntity<Void> callbackBoleto(@RequestBody List<BancoInterCallbackBoletoDTO> boleto) {
        this.pagamentoService.callbackBoleto(boleto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
