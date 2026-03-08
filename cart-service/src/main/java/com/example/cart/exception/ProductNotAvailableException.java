package com.example.cart.exception;

public class ProductNotAvailableException extends RuntimeException{

	public ProductNotAvailableException(Integer id) {
        super("El producto no está disponible: " + id);
    }
}
