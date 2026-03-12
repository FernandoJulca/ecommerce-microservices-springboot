package com.example.payment.exception;

public class PaymentNotFoundException extends RuntimeException{

	public PaymentNotFoundException(Integer orderId) {
        super("Pago no encontrado para la orden: " + orderId);
    }
}
