package com.example.cart.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.cart.dto.AddItemRequest;
import com.example.cart.dto.CartItemResponse;
import com.example.cart.dto.CartResponse;
import com.example.cart.dto.ProductResponse;
import com.example.cart.exception.CartItemNotFoundException;
import com.example.cart.exception.CartNotFoundException;
import com.example.cart.exception.InsufficientStockException;
import com.example.cart.exception.ProductNotAvailableException;
import com.example.cart.feign.ProductServiceClient;
import com.example.cart.model.Cart;
import com.example.cart.model.CartItem;
import com.example.cart.repository.ICartItemRepository;
import com.example.cart.repository.ICartRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {


    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;

    public CartResponse getCart(String userEmail) {
        Cart cart = getOrCreateCart(userEmail);
        return toResponse(cart);
    }

    public CartResponse addItem(String userEmail, AddItemRequest request) {
        // Verifica que el producto existe y tiene stock
        ProductResponse product = productServiceClient.getProductById(request.getProductId());

        if (!product.isActive()) {
            throw new ProductNotAvailableException(request.getProductId());
        }

        if (product.getStock() < request.getQuantity()) {
            throw new InsufficientStockException(product.getName(), product.getStock());
        }

        Cart cart = getOrCreateCart(userEmail);

        // Si el producto ya está en el carrito, suma la cantidad
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), request.getProductId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
            log.info("Cantidad actualizada en carrito para: {}", userEmail);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(product.getId())
                    .productName(product.getName())
                    .productPrice(product.getPrice())
                    .quantity(request.getQuantity())
                    .imageUrl(product.getImageUrl())
                    .build();
            cartItemRepository.save(newItem);
            log.info("Producto agregado al carrito de: {}", userEmail);
        }

        return toResponse(cartRepository.findByUserEmail(userEmail).get());
    }

    public CartResponse removeItem(String userEmail, Integer itemId) {
        Cart cart = cartRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new CartNotFoundException(userEmail));

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException(itemId));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        log.info("Item eliminado del carrito de: {}", userEmail);

        return toResponse(cartRepository.findByUserEmail(userEmail).get());
    }

    public void clearCart(String userEmail) {
        Cart cart = cartRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new CartNotFoundException(userEmail));
        cart.getItems().clear();
        cartRepository.save(cart);
        log.info("Carrito vaciado para: {}", userEmail);
    }

    // Crea el carrito si no existe
    private Cart getOrCreateCart(String userEmail) {
        return cartRepository.findByUserEmail(userEmail)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userEmail(userEmail)
                            .build();
                    log.info("Carrito creado para: {}", userEmail);
                    return cartRepository.save(newCart);
                });
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(item -> CartItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productPrice(item.getProductPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getProductPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .imageUrl(item.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .userEmail(cart.getUserEmail())
                .items(items)
                .total(total)
                .totalItems(items.size())
                .build();
    }
}
