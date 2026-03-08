package com.example.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.cart.dto.ProductResponse;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

	@GetMapping("/products/{id}")
	ProductResponse getProductById(@PathVariable Integer id);
}
