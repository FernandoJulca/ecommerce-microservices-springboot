package com.example.order.exception;

public class EmptyCartException extends RuntimeException {

	public EmptyCartException(String email) {
		super("El carrito está vacío para: " + email);
	}
}
