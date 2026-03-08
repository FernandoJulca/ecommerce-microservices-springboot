package com.example.cart.exception;

public class InsufficientStockException extends RuntimeException{

	public InsufficientStockException(String name, Integer stock) {
        super("Stock insuficiente para: " + name + ". Stock disponible: " + stock);
    }
}
