package com.example.payment.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.payment.dto.OrderResponse;
import com.example.payment.dto.PaymentRequest;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.exception.InvalidOrderStatusForPaymentException;
import com.example.payment.exception.InvalidPaymentMethodException;
import com.example.payment.exception.PaymentAlreadyExistsException;
import com.example.payment.exception.PaymentNotFoundException;
import com.example.payment.exception.UnauthorizedPaymentException;
import com.example.payment.feign.OrderServiceClient;
import com.example.payment.feign.UpdateStatusRequest;
import com.example.payment.model.Payment;
import com.example.payment.model.PaymentMethod;
import com.example.payment.model.PaymentStatus;
import com.example.payment.repository.IPaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

	private final IPaymentRepository paymentRepository;
    private final OrderServiceClient orderServiceClient;
    private final EmailService emailService;

    public PaymentResponse processPayment(String userEmail, PaymentRequest request) {
        log.info("Procesando pago para orden: {}", request.getOrderId());

        // Verificar que no se haya pagado ya
        if (paymentRepository.existsByOrderId(request.getOrderId())) {
            throw new PaymentAlreadyExistsException(request.getOrderId());
        }

        // Obtener la orden
        OrderResponse order = orderServiceClient.getOrderById(userEmail, request.getOrderId());

        // Verificar que la orden pertenece al usuario
        if (!order.getUserEmail().equals(userEmail)) {
            throw new UnauthorizedPaymentException(request.getOrderId());
        }

        // Verificar que la orden está en PENDING
        if (!order.getStatus().equals("PENDING")) {
            throw new InvalidOrderStatusForPaymentException(order.getStatus());
        }

        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPaymentMethodException(request.getPaymentMethod());
        }

        // Crear el pago en PENDING
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .userEmail(userEmail)
                .amount(order.getTotal())
                .paymentMethod(method)
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        // Simular procesamiento del pago
        boolean paymentSuccess = simulatePayment(method);

        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(UUID.randomUUID().toString().substring(0, 12).toUpperCase());
            paymentRepository.save(payment);
            log.info("Pago completado. TransactionId: {}", payment.getTransactionId());

            // Actualizar estado de la orden a CONFIRMED
            orderServiceClient.updateStatus(
                "ADMIN",
                request.getOrderId(),
                new UpdateStatusRequest("CONFIRMED")
            );

            // Enviar email de confirmación
            emailService.sendPaymentConfirmation(payment, order);

        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.warn("Pago fallido para orden: {}", request.getOrderId());

            // Enviar email de fallo
            emailService.sendPaymentFailed(payment);
        }

        return toResponse(payment);
    }

    public PaymentResponse getPaymentByOrder(Integer orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));
        return toResponse(payment);
    }

    public List<PaymentResponse> getMyPayments(String userEmail) {
        return paymentRepository.findByUserEmail(userEmail)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Simulación — 90% de éxito
    private boolean simulatePayment(PaymentMethod method) {
        return Math.random() > 0.1;
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userEmail(payment.getUserEmail())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .status(payment.getStatus().name())
                .transactionId(payment.getTransactionId())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
