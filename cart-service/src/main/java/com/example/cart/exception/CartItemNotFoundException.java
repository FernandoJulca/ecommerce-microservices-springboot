package com.example.cart.exception;

public class CartItemNotFoundException extends RuntimeException{

	public CartItemNotFoundException(Integer id) {
		super("Item no encontrado en el carrito con id: " + id);
	}
}
