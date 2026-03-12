package com.example.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

	@NotNull(message = "El orderId es obligatorio")
    private Integer orderId;

    @NotNull(message = "El método de pago es obligatorio")
    private String paymentMethod;
}
