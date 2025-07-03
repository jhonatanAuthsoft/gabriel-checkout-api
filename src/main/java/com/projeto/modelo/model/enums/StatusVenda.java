package com.projeto.modelo.model.enums;

public enum StatusVenda {
    PENDENTE, // Finalizou, mas não concluiu o pagamento
    FINALIZADO, // Concluiu o pagamento
    CANCELADO, // Cancelou o pedido, fica em sincronia com o StatusPagamento, caso ele seja REEMBOLSADO, o pedido é cancelado
    CARRINHO_ABANDONADO  // Passou da fase de cadastro mas não deu prosseguimento
}
