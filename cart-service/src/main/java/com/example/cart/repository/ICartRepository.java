package com.example.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cart.model.Cart;

@Repository
public interface ICartRepository extends JpaRepository<Cart, Integer>{

	Optional<Cart> findByUserEmail(String userEmail);
}
