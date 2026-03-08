package com.example.order.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

	private Integer id;
    private String userEmail;
    private List<CartItemResponse> items;
    private BigDecimal total;
    private Integer totalItems;
}
