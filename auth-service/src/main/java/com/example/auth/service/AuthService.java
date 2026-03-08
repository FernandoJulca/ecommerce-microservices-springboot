package com.example.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.auth.dto.AuthResponse;
import com.example.auth.dto.CreateProfileRequest;
import com.example.auth.dto.LoginRequest;
import com.example.auth.dto.RegisterRequest;
import com.example.auth.enums.Role;
import com.example.auth.exception.EmailAlreadyExistsException;
import com.example.auth.exception.InvalidCredentialsException;
import com.example.auth.feign.UserServiceClient;
import com.example.auth.model.User;
import com.example.auth.repository.IUserRepository;
import com.example.auth.security.JwtUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserServiceClient userServiceClient;

    public AuthResponse register(RegisterRequest request) {
        log.info("Intentando registrar usuario: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .build();

        userRepository.save(user);
        log.info("Usuario registrado exitosamente: {}", user.getEmail());

        // Llama a user-service para crear el perfil
        try {
            userServiceClient.createProfile(
                CreateProfileRequest.builder()
                    .authId(user.getId())
                    .email(user.getEmail())
                    .build()
            );
            log.info("Perfil creado en user-service para: {}", user.getEmail());
        } catch (Exception e) {
            // Si user-service falla, igual devolvemos el token
            // El perfil se puede crear después
            log.warn("No se pudo crear perfil en user-service: {}", e.getMessage());
        }
        
        String token = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Intento de login: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        log.info("Login exitoso: {}", user.getEmail());

        String token = jwtUtil.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
