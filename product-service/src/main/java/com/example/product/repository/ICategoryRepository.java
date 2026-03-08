package com.example.product.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.product.model.Category;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Integer>{

	Optional<Category> findByName(String name);

	boolean existsByName(String name);
}
