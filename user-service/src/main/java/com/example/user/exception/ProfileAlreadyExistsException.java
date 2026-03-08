package com.example.user.exception;

public class ProfileAlreadyExistsException extends RuntimeException{

	public ProfileAlreadyExistsException(String email) {
		super("El usuario ya esta registrado con el id: " + email);
	}
}
