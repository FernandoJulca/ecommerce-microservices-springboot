package com.example.payment.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

	private Integer id;
    private String productName;
    private Integer quantity;
    private BigDecimal subtotal;
}
