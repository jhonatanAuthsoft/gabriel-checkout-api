package com.projeto.modelo.controller;

import com.projeto.modelo.controller.dto.request.PagamentoRequestDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.boleto.BancoInterCallbackBoletoDTO;
import com.projeto.modelo.controller.dto.request.bancoInter.pix.calback.BancoInterCallbackPixDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.boleto.BancoInterBoletoPDFResponseDTO;
import com.projeto.modelo.controller.dto.response.bancoInter.pix.BancoInterPixResponseDTO;
import com.projeto.modelo.service.PagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<BancoInterBoletoPDFResponseDTO> pagarBoleto(@RequestBody PagamentoRequestDTO pagamentoRequestDTO) {
        return new ResponseEntity<>(this.pagamentoService.pagarComBoleto(pagamentoRequestDTO), HttpStatus.OK);
    }
    /*
    @PostMapping("/cartao")
    public ResponseEntity<BancoInterPixResponseDTO> pagarPix(@RequestBody PagamentoRequestDTO pagamentoRequestDTO) {
        return new ResponseEntity<>(this.pagamentoService.pagarComPix(pagamentoRequestDTO), HttpStatus.OK);
    }
    */
    @PostMapping("/callback/pix")
    public ResponseEntity<Void> callbackPix(@RequestBody List<BancoInterCallbackPixDTO> bancoInterCallbackPixDTO) {
        this.pagamentoService.callbackPix(bancoInterCallbackPixDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/callback/boleto")
    public ResponseEntity<Void> callbackBoleto(@RequestBody List<BancoInterCallbackBoletoDTO> bancoInterCallbackBoleto) {
        this.pagamentoService.callbackBoleto(bancoInterCallbackBoleto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
    @PostMapping("/callback/cartao")
    public ResponseEntity<Void> callbackBoleto(@RequestBody List<BancoInterCallbackBoletoDTO> bancoInterCallbackBoleto) {
        this.pagamentoService.callbackCartao(bancoInterCallbackBoleto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
     */
}
