package com.example.payment.exception;

public class InvalidOrderStatusForPaymentException extends RuntimeException{

	public InvalidOrderStatusForPaymentException(String status) {
        super("No se puede pagar una orden en estado: " + status);
    }
}
