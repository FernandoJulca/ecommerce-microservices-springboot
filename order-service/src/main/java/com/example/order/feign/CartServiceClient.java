package com.example.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.order.dto.CartResponse;

@FeignClient(name = "cart-service")
public interface CartServiceClient {

	@GetMapping("/cart")
    CartResponse getCart(@RequestHeader("X-Username") String email);

    @DeleteMapping("/cart")
    void clearCart(@RequestHeader("X-Username") String email);
}
