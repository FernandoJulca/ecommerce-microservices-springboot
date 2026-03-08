package com.example.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProfileRequest {

	@NotNull
	private Integer authId;
	
	@NotBlank(message = "Email requerido")
	private String email;
}
