package com.example.payment.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

	private Integer id;
    private String userEmail;
    private String status;
    private BigDecimal total;
    private String shippingAddress;
    private List<OrderItemResponse> items;
}
