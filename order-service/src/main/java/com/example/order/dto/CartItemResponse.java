package com.example.order.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

	private Integer id;
	private Integer productId;
	private String productName;
	private BigDecimal productPrice;
	private Integer quantity;
	private BigDecimal subtotal;
	private String imageUrl;
}
