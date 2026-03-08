package com.example.product.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.product.dto.ProductRequest;
import com.example.product.dto.ProductResponse;
import com.example.product.exception.CategoryNotFoundException;
import com.example.product.exception.InsufficientStockException;
import com.example.product.exception.ProductNotFoundException;
import com.example.product.model.Category;
import com.example.product.model.Product;
import com.example.product.repository.ICategoryRepository;
import com.example.product.repository.IProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

	private final IProductRepository _productRepository;
    private final ICategoryRepository _categoryRepository;
    private final CloudinaryService _cloudinaryService;

    public ProductResponse create(ProductRequest request, MultipartFile image) {
        Category category = _categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .active(true)
                .build();

        // Subir imagen si se proporcionó
        if (image != null && !image.isEmpty()) {
            Map result = _cloudinaryService.uploadImage(image);
            product.setImageUrl((String) result.get("secure_url"));
            product.setImagePublicId((String) result.get("public_id"));
        }

        _productRepository.save(product);
        log.info("Producto creado: {}", product.getName());
        return toResponse(product);
    }

    public List<ProductResponse> getAll() {
        return _productRepository.findByActiveTrue()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ProductResponse> getByCategory(Integer categoryId) {
        return _productRepository.findByCategoryIdAndActiveTrue(categoryId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ProductResponse> search(String name) {
        return _productRepository.findByNameContainingIgnoreCaseAndActiveTrue(name)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ProductResponse getById(Integer id) {
        Product product = _productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toResponse(product);
    }

    public ProductResponse update(Integer id, ProductRequest request, MultipartFile image) {
        Product product = _productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Category category = _categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);

        // Si hay imagen nueva, borra la vieja y sube la nueva
        if (image != null && !image.isEmpty()) {
            if (product.getImagePublicId() != null) {
                _cloudinaryService.deleteImage(product.getImagePublicId());
            }
            Map result = _cloudinaryService.uploadImage(image);
            product.setImageUrl((String) result.get("secure_url"));
            product.setImagePublicId((String) result.get("public_id"));
        }

        _productRepository.save(product);
        log.info("Producto actualizado: {}", product.getName());
        return toResponse(product);
    }

    public void delete(Integer id) {
        Product product = _productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        // Borrado lógico
        product.setActive(false);

        // Borra imagen de Cloudinary
        if (product.getImagePublicId() != null) {
            _cloudinaryService.deleteImage(product.getImagePublicId());
        }

        _productRepository.save(product);
        log.info("Producto desactivado: {}", product.getName());
    }
    
    public void reduceStock(Integer id, Integer quantity) {
        Product product = _productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        
        if (product.getStock() < quantity) {
            throw new InsufficientStockException(product.getName(), product.getStock());
        }
        
        product.setStock(product.getStock() - quantity);
        _productRepository.save(product);
        log.info("Stock reducido para producto {}: -{}", id, quantity);
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .active(product.isActive())
                .build();
    }
}
