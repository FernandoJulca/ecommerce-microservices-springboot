package com.example.order.sevice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.order.dto.CartResponse;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.OrderItemResponse;
import com.example.order.dto.OrderResponse;
import com.example.order.dto.UpdateOrderStatusRequest;
import com.example.order.exception.EmptyCartException;
import com.example.order.exception.InvalidOrderStatusException;
import com.example.order.exception.OrderNotFoundException;
import com.example.order.exception.UnauthorizedOrderAccessException;
import com.example.order.feign.CartServiceClient;
import com.example.order.feign.ProductServiceClient;
import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.model.OrderStatus;
import com.example.order.repository.IOrderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

	private final IOrderRepository _orderRepository;
    private final CartServiceClient _cartServiceClient;
    private final ProductServiceClient _productServiceClient;

    public OrderResponse createOrder(String userEmail, CreateOrderRequest request) {
        log.info("Creando pedido para: {}", userEmail);

        // 1. Obtener el carrito del usuario
        CartResponse cart = _cartServiceClient.getCart(userEmail);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException(userEmail);
        }

        // 2. Crear la orden
        Order order = Order.builder()
                .userEmail(userEmail)
                .status(OrderStatus.PENDING)
                .total(cart.getTotal())
                .shippingAddress(request.getShippingAddress())
                .build();

        // 3. Crear los items de la orden desde el carrito
        List<OrderItem> items = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .productId(cartItem.getProductId())
                        .productName(cartItem.getProductName())
                        .productPrice(cartItem.getProductPrice())
                        .quantity(cartItem.getQuantity())
                        .subtotal(cartItem.getSubtotal())
                        .imageUrl(cartItem.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        order.setItems(items);
        _orderRepository.save(order);
        log.info("Orden creada con id: {}", order.getId());

        // 4. Reducir stock de cada producto
        cart.getItems().forEach(item -> {
            try {
                _productServiceClient.reduceStock(item.getProductId(), item.getQuantity());
            } catch (Exception e) {
                log.warn("No se pudo reducir stock del producto {}: {}", 
                    item.getProductId(), e.getMessage());
            }
        });

        // 5. Vaciar el carrito
        try {
            _cartServiceClient.clearCart(userEmail);
            log.info("Carrito vaciado para: {}", userEmail);
        } catch (Exception e) {
            log.warn("No se pudo vaciar el carrito: {}", e.getMessage());
        }

        return toResponse(order);
    }

    public List<OrderResponse> getMyOrders(String userEmail) {
        return _orderRepository.findByUserEmailOrderByCreatedAtDesc(userEmail)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Integer id, String userEmail) {
        Order order = _orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (!order.getUserEmail().equals(userEmail)) {
            throw new UnauthorizedOrderAccessException(id);
        }

        return toResponse(order);
    }

    // Solo ADMIN
    public OrderResponse updateStatus(Integer id, UpdateOrderStatusRequest request) {
        Order order = _orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        try {
            OrderStatus newStatus = OrderStatus.valueOf(request.getStatus().toUpperCase());
            order.setStatus(newStatus);
            order.setUpdatedAt(LocalDateTime.now());
            _orderRepository.save(order);
            log.info("Estado de orden {} actualizado a: {}", id, newStatus);
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderStatusException(request.getStatus());
        }

        return toResponse(order);
    }

    // Solo ADMIN
    public List<OrderResponse> getAllOrders() {
        return _orderRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .productPrice(item.getProductPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .imageUrl(item.getImageUrl())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .userEmail(order.getUserEmail())
                .status(order.getStatus().name())
                .total(order.getTotal())
                .shippingAddress(order.getShippingAddress())
                .items(items)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
