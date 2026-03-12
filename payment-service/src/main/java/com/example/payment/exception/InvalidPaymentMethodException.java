package com.example.payment.exception;

public class InvalidPaymentMethodException extends RuntimeException{

	public InvalidPaymentMethodException(String method) {
        super("Método de pago inválido: " + method + ". Use: CREDIT_CARD, DEBIT_CARD, PAYPAL");
    }
}
