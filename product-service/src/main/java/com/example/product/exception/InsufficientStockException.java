package com.example.product.exception;

public class InsufficientStockException extends RuntimeException{

	public InsufficientStockException(String name, Integer stock) {
        super("Stock insuficiente para: " + name + ". Stock disponible: " + stock);
    }
}
