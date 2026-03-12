package com.example.payment.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.payment.dto.OrderResponse;
import com.example.payment.model.Payment;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

	private final JavaMailSender mailSender;

    public void sendPaymentConfirmation(Payment payment, OrderResponse order) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(payment.getUserEmail());
            helper.setSubject("✅ Confirmación de pago - Orden #" + payment.getOrderId());
            helper.setText(buildEmailBody(payment, order), true);

            mailSender.send(message);
            log.info("Email de confirmación enviado a: {}", payment.getUserEmail());

        } catch (Exception e) {
            log.error("Error enviando email: {}", e.getMessage());
        }
    }

    public void sendPaymentFailed(Payment payment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(payment.getUserEmail());
            helper.setSubject("❌ Pago fallido - Orden #" + payment.getOrderId());
            helper.setText(buildFailedEmailBody(payment), true);

            mailSender.send(message);
            log.info("Email de fallo enviado a: {}", payment.getUserEmail());

        } catch (Exception e) {
            log.error("Error enviando email: {}", e.getMessage());
        }
    }

    private String buildEmailBody(Payment payment, OrderResponse order) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>¡Tu pago fue confirmado!</h2>");
        sb.append("<p>Gracias por tu compra. Aquí está el resumen:</p>");
        sb.append("<hr>");
        sb.append("<p><b>Orden #:</b> ").append(order.getId()).append("</p>");
        sb.append("<p><b>Transacción #:</b> ").append(payment.getTransactionId()).append("</p>");
        sb.append("<p><b>Método de pago:</b> ").append(payment.getPaymentMethod()).append("</p>");
        sb.append("<p><b>Dirección de envío:</b> ").append(order.getShippingAddress()).append("</p>");
        sb.append("<hr>");
        sb.append("<h3>Productos:</h3><ul>");
        order.getItems().forEach(item ->
            sb.append("<li>")
              .append(item.getProductName())
              .append(" x").append(item.getQuantity())
              .append(" — S/ ").append(item.getSubtotal())
              .append("</li>")
        );
        sb.append("</ul>");
        sb.append("<hr>");
        sb.append("<h3>Total pagado: S/ ").append(payment.getAmount()).append("</h3>");
        sb.append("<p>Tu pedido está siendo procesado. ¡Gracias!</p>");
        return sb.toString();
    }

    private String buildFailedEmailBody(Payment payment) {
        return "<h2>❌ Tu pago no pudo procesarse</h2>" +
               "<p>Hubo un problema con el pago de la orden <b>#" + payment.getOrderId() + "</b>.</p>" +
               "<p>Por favor intenta nuevamente con otro método de pago.</p>";
    }
}
