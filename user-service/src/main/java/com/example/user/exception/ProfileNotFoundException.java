package com.example.user.exception;

public class ProfileNotFoundException extends RuntimeException{

	public ProfileNotFoundException(String email) {
		super("El perfil no fue encontrado : " + email );
	}
}
