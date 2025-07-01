package com.projeto.modelo.model.entity.produto;

import jakarta.persistence.Column;
import lombok.Builder;

@Builder
public record SuporteGarantia(
        String email,
        @Column(name = "telefone_suporte")
        String telefoneSuporte,
        @Column(name = "mostrar_telefone_suporte")
        Boolean mostrarTelefoneSuporte,
        @Column(name = "whatsapp_suporte")
        String whatsappSuporte,
        @Column(name = "mostrar_whatsapp_suporte")
        Boolean mostrarWhatsappSuporte
) {
}