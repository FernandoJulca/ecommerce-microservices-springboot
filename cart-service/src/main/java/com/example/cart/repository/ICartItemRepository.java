package com.example.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.cart.model.CartItem;

@Repository
public interface ICartItemRepository extends JpaRepository<CartItem, Integer>{

	Optional<CartItem> findByCartIdAndProductId(Integer cartId, Integer productId);
}
