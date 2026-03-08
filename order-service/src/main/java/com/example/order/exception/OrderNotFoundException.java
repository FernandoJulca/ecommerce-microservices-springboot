package com.example.order.exception;

public class OrderNotFoundException extends RuntimeException{

	public OrderNotFoundException(Integer id) {
        super("Orden no encontrada con id: " + id);
    }
}
