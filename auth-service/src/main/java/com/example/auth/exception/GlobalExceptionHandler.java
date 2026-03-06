package com.example.auth.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.auth.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleEmailExists(EmailAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(
				ErrorResponse.builder().status(409).message(ex.getMessage()).timestamp(LocalDateTime.now()).build());
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
				ErrorResponse.builder().status(401).message(ex.getMessage()).timestamp(LocalDateTime.now()).build());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		String errors = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(", "));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ErrorResponse.builder().status(400).message(errors).timestamp(LocalDateTime.now()).build());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.builder().status(500)
				.message("Error interno del servidor").timestamp(LocalDateTime.now()).build());
	}
}
