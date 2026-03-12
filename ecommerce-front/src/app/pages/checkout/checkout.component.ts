import { Component, OnInit } from '@angular/core';
import { Cart } from '../../models/cart.model';
import { Order } from '../../models/order.model';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { PaymentService } from '../../core/services/payment.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-checkout',
  imports: [CommonModule,FormsModule,RouterModule],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.scss'
})
export class CheckoutComponent implements OnInit{

  cart: Cart | null = null;
  order: Order | null = null;
  loading = false;
  processingPayment = false;
  errorMessage = '';

  // Paso actual: 1=dirección, 2=pago, 3=confirmación
  currentStep = 1;

  // Formulario
  shippingAddress = '';
  paymentMethod = 'CREDIT_CARD';

  paymentMethods = [
    { value: 'CREDIT_CARD', label: '💳 Tarjeta de crédito' },
    { value: 'DEBIT_CARD', label: '💳 Tarjeta de débito' },
    { value: 'PAYPAL', label: '🅿️ PayPal' }
  ];

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private paymentService: PaymentService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.loading = true;
    this.cartService.getCart().subscribe({
      next: cart => {
        if (!cart || cart.items.length === 0) {
          this.router.navigate(['/cart']);
          return;
        }
        this.cart = cart;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        this.router.navigate(['/cart']);
      }
    });
  }

  // Paso 1 → Paso 2
  goToPayment(): void {
    if (!this.shippingAddress.trim()) {
      this.errorMessage = 'Por favor ingresa tu dirección de envío';
      return;
    }
    this.errorMessage = '';
    this.currentStep = 2;
  }

  // Paso 2 → Crear orden y pagar
  confirmPayment(): void {
    this.processingPayment = true;
    this.errorMessage = '';

    // Primero crea la orden
    this.orderService.createOrder({ shippingAddress: this.shippingAddress }).subscribe({
      next: order => {
        this.order = order;

        // Luego procesa el pago
        this.paymentService.processPayment({
          orderId: order.id,
          paymentMethod: this.paymentMethod
        }).subscribe({
          next: payment => {
            this.processingPayment = false;
            if (payment.status === 'COMPLETED') {
              this.currentStep = 3;
            } else {
              this.errorMessage = 'El pago no pudo procesarse, intenta de nuevo';
            }
          },
          error: () => {
            this.processingPayment = false;
            this.errorMessage = 'Error al procesar el pago, intenta de nuevo';
          }
        });
      },
      error: (err) => {
        this.processingPayment = false;
        if (err.status === 400) {
          this.errorMessage = 'Tu carrito está vacío';
        } else {
          this.errorMessage = 'Error al crear el pedido, intenta de nuevo';
        }
      }
    });
  }

  goToOrders(): void {
    this.router.navigate(['/orders']);
  }
}
