package com.example.cart.exception;

public class CartNotFoundException extends RuntimeException{

	public CartNotFoundException(String email) {
        super("Carrito no encontrado para: " + email);
    }
}
