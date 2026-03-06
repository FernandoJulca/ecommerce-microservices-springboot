package com.example.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

	@NotBlank(message = "El email es obligatorio")
	@Email(message = "El email no tiene un formato valido")
	private String email;
	
	@NotBlank(message = "La contraseña es obligatoria")
	@Size(min = 5, message = "La contraseña debe tener al menos 5 caracteres")
	private String password;
}
