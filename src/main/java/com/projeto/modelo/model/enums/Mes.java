package com.projeto.modelo.model.enums;

public enum Mes {
    JANEIRO,
    FEVEREIRO,
    MARCO,
    ABRIL,
    MAIO,
    JUNHO,
    JULHO,
    AGOSTO,
    SETEMBRO,
    OUTUBRO,
    NOVEMBRO,
    DEZEMBRO;

    public static Mes fromNumber(int n) {
        if (n < 1 || n > 12) throw new IllegalArgumentException("Mês inválido: " + n);
        return Mes.values()[n - 1];
    }
}
