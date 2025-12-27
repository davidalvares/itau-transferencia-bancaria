package com.itau.app.transferencia.domain.constant;

public class MensagensConstant {

    public static final String LIMITE_TRANSFERENCIA_EXCEDIDO = "Valor excede o limite de R$ 10.000,00";
    public static final String CONTA_ORIGEM_NAO_ENCONTRADA = "Conta de origem não encontrada";
    public static final String CONTA_DESTINO_NAO_ENCONTRADA = "Conta de destino não encontrada";
    public static final String SALDO_INSUFICIENTE = "Saldo insuficiente";
    public static final String CONTA_JA_EXISTE = "Conta já existe";

    private MensagensConstant() {
        // Construtor privado para evitar instanciacao
    }
}
