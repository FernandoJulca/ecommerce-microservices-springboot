package com.example.product.exception;

public class CategoryNotFoundException extends RuntimeException{

	public CategoryNotFoundException(Integer id) {
        super("Categoría no encontrada con id: " + id);
    }
}
