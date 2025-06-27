package com.projeto.modelo.util;

public class StringUtils {

    /**
     * Verifica se uma string é nula ou vazia (após remover espaços em branco).
     * @param str A string a ser verificada.
     * @return true se a string for null ou vazia, false caso contrário.
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean cpfOuCnpjValido(String documento) {
        documento = documento.replaceAll("[^0-9]", "");

        if (documento.length() == 11) {
            // Validação de CPF
            if (documento.equals(documento.substring(0, 1).repeat(11))) {
                return false;
            }
            int soma1 = 0;
            for (int i = 0; i < 9; i++) {
                soma1 += Character.getNumericValue(documento.charAt(i)) * (10 - i);
            }
            int digito1 = 11 - (soma1 % 11);
            if (digito1 >= 10) digito1 = 0;

            int soma2 = 0;
            for (int i = 0; i < 9; i++) {
                soma2 += Character.getNumericValue(documento.charAt(i)) * (11 - i);
            }
            soma2 += digito1 * 2;
            int digito2 = 11 - (soma2 % 11);
            if (digito2 >= 10) digito2 = 0;

            return documento.charAt(9) == (char) ('0' + digito1) && documento.charAt(10) == (char) ('0' + digito2);
        } else if (documento.length() == 14) {
            // Validação de CNPJ
            if (documento.equals(documento.substring(0, 1).repeat(14))) {
                return false;
            }
            int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

            int soma1 = 0;
            for (int i = 0; i < 12; i++) {
                soma1 += Character.getNumericValue(documento.charAt(i)) * pesos1[i];
            }
            int digito1 = 11 - (soma1 % 11);
            if (digito1 >= 10) digito1 = 0;

            int soma2 = 0;
            for (int i = 0; i < 13; i++) {
                soma2 += Character.getNumericValue(documento.charAt(i)) * pesos2[i];
            }
            int digito2 = 11 - (soma2 % 11);
            if (digito2 >= 10) digito2 = 0;

            return documento.charAt(12) == (char) ('0' + digito1) && documento.charAt(13) == (char) ('0' + digito2);
        }

        return false; // Não é CPF nem CNPJ válido
    }

    public static String removerCaracteresEspeciais(String texto) {
        if (texto == null) return null;

        // Remove tudo que não for dígito
        return texto.replaceAll("[^\\d]", "");
    }

    public static boolean cepValido(String cep) {
        if (cep == null) {
            return false;
        }
        cep = cep.replaceAll("[^\\d]", "");
        return cep.matches("\\d{8}");
    }
}
