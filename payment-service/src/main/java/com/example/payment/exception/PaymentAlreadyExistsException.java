package com.example.payment.exception;

public class PaymentAlreadyExistsException extends RuntimeException{

	public PaymentAlreadyExistsException(Integer orderId) {
        super("La orden ya fue pagada: " + orderId);
    }
}
