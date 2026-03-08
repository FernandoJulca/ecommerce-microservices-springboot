package com.example.user.service;

import org.springframework.stereotype.Service;

import com.example.user.dto.CreateProfileRequest;
import com.example.user.dto.UpdateProfileRequest;
import com.example.user.dto.UserProfileResponse;
import com.example.user.exception.ProfileAlreadyExistsException;
import com.example.user.exception.ProfileNotFoundException;
import com.example.user.model.User;
import com.example.user.repository.IUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final IUserRepository _userProfileRepository;

    public void createProfile(CreateProfileRequest request) {
        log.info("Creando perfil para: {}", request.getEmail());

        if (_userProfileRepository.existsByEmail(request.getEmail())) {
            throw new ProfileAlreadyExistsException(request.getEmail());
        }

        User profile = User.builder()
                .authId(request.getAuthId())
                .email(request.getEmail())
                .build();

        _userProfileRepository.save(profile);
        log.info("Perfil creado exitosamente para: {}", request.getEmail());
    }

    public UserProfileResponse getProfile(String email) {
        log.info("Buscando perfil para: {}", email);

        User profile = _userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new ProfileNotFoundException(email));

        return toResponse(profile);
    }

    public UserProfileResponse updateProfile(String email, UpdateProfileRequest request) {
        log.info("Actualizando perfil para: {}", email);

        User profile = _userProfileRepository.findByEmail(email)
                .orElseThrow(() -> new ProfileNotFoundException(email));

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());

        _userProfileRepository.save(profile);
        log.info("Perfil actualizado para: {}", email);

        return toResponse(profile);
    }

    private UserProfileResponse toResponse(User profile) {
        return UserProfileResponse.builder()
                .authId(profile.getId())
                .email(profile.getEmail())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .phone(profile.getPhone())
                .address(profile.getAddress())
                .build();
    }
}
