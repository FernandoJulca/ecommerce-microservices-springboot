import { Component, OnInit } from '@angular/core';
import { Order } from '../../models/order.model';
import { OrderService } from '../../core/services/order.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-orders',
  imports: [CommonModule,RouterModule],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.scss'
})
export class OrdersComponent implements OnInit{

  orders: Order[] = [];
  loading = false;
  selectedOrder: Order | null = null;

  statusLabels: Record<string, string> = {
    PENDING:   '🕐 Pendiente',
    CONFIRMED: '✅ Confirmado',
    SHIPPED:   '🚚 Enviado',
    DELIVERED: '📦 Entregado',
    CANCELLED: '❌ Cancelado'
  };

  statusColors: Record<string, string> = {
    PENDING:   'warning',
    CONFIRMED: 'success',
    SHIPPED:   'info',
    DELIVERED: 'primary',
    CANCELLED: 'danger'
  };

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.orderService.getMyOrders().subscribe({
      next: orders => {
        this.orders = orders.sort((a, b) =>
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  toggleDetail(order: Order): void {
    this.selectedOrder = this.selectedOrder?.id === order.id ? null : order;
  }
}
