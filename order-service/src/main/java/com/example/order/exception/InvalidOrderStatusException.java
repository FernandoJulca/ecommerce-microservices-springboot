package com.example.order.exception;

public class InvalidOrderStatusException extends RuntimeException{

	public InvalidOrderStatusException(String status) {
        super("Estado de orden inválido: " + status);
    }
}
