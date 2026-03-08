package com.example.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {

	@NotBlank(message = "Nombre requerido")
	private String firstName;
	@NotBlank(message = "Apellido requerido")
	private String lastName;
	private String phone;
	private String address;
} 
