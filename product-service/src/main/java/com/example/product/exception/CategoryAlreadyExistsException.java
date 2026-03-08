package com.example.product.exception;

public class CategoryAlreadyExistsException extends RuntimeException{

	 public CategoryAlreadyExistsException(String name) {
	        super("Ya existe una categoría con el nombre: " + name);
	    }
}
