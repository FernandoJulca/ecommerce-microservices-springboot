package com.example.product.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.product.dto.CategoryRequest;
import com.example.product.dto.CategoryResponse;
import com.example.product.exception.CategoryAlreadyExistsException;
import com.example.product.exception.CategoryNotFoundException;
import com.example.product.model.Category;
import com.example.product.repository.ICategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

	private final ICategoryRepository _categoryRepository;

    public CategoryResponse create(CategoryRequest request) {
        if (_categoryRepository.existsByName(request.getName())) {
            throw new CategoryAlreadyExistsException(request.getName());
        }
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        _categoryRepository.save(category);
        return toResponse(category);
    }

    public List<CategoryResponse> getAll() {
        return _categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void delete(Integer id) {
        Category category = _categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        _categoryRepository.delete(category);
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
}
