package com.example.product.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

	private final Cloudinary cloudinary;

	public Map uploadImage(MultipartFile file) {
		try {
			Map result = cloudinary.uploader().upload(file.getBytes(),
					ObjectUtils.asMap("folder", "ecommerce/products"));
			log.info("Imagen subida a Cloudinary: {}", result.get("public_id"));
			return result;
		} catch (IOException e) {
			log.error("Error subiendo imagen a Cloudinary: {}", e.getMessage());
			throw new RuntimeException("Error al subir imagen");
		}
	}

	public void deleteImage(String publicId) {
		try {
			cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
			log.info("Imagen eliminada de Cloudinary: {}", publicId);
		} catch (IOException e) {
			log.error("Error eliminando imagen de Cloudinary: {}", e.getMessage());
		}
	}
}
