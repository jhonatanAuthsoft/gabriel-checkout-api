package com.projeto.modelo.controller.dto.request.bancoInter.pix.calback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.projeto.modelo.controller.dto.request.bancoInter.boleto.BancoInterCallbackBoletoDTO;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BancoInterCallbackPix {
    List<BancoInterCallbackPixDTO> pix;
}
