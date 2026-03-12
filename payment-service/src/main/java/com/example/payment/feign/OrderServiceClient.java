package com.example.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.payment.dto.OrderResponse;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

	@GetMapping("/orders/{id}")
    OrderResponse getOrderById(
        @RequestHeader("X-Username") String email,
        @PathVariable Integer id);

    @PutMapping("/orders/{id}/status")
    void updateStatus(
        @RequestHeader("X-Role") String role,
        @PathVariable Integer id,
        @RequestBody UpdateStatusRequest request);
}
