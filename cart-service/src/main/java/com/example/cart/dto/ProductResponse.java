package com.example.cart.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

	private Integer id;
	private String name;
	private String description;
	private BigDecimal price;
	private Integer stock;
	private String imageUrl;
	private String categoryName;
	private boolean active;
}
