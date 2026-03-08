package com.example.order.exception;

public class UnauthorizedOrderAccessException extends RuntimeException{

	public UnauthorizedOrderAccessException(Integer id) {
        super("No tienes permiso para ver la orden: " + id);
    }
}
