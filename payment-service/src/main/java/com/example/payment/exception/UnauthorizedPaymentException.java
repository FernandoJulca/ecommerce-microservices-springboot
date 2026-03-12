package com.example.payment.exception;

public class UnauthorizedPaymentException extends RuntimeException{

	public UnauthorizedPaymentException(Integer orderId) {
        super("No tienes permiso para pagar la orden: " + orderId);
    }
}
