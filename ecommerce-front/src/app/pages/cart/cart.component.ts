import { Component, OnInit } from '@angular/core';
import { Cart } from '../../models/cart.model';
import { CartService } from '../../core/services/cart.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-cart',
  imports: [CommonModule, RouterModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.scss'
})
export class CartComponent implements OnInit{

   cart: Cart | null = null;
  loading = false;
  removingItem: number | null = null;

  constructor(private cartService: CartService) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.loading = true;
    this.cartService.getCart().subscribe({
      next: cart => {
        this.cart = cart;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  removeItem(itemId: number): void {
    this.removingItem = itemId;
    this.cartService.removeItem(itemId).subscribe({
      next: cart => {
        this.cart = cart;
        this.removingItem = null;
      },
      error: () => this.removingItem = null
    });
  }

  clearCart(): void {
    if (!confirm('¿Estás seguro de vaciar el carrito?')) return;

    this.cartService.clearCart().subscribe({
      next: () => {
        this.cart = null;
        this.cartService['cartCount'].next(0);
        this.loadCart();
      }
    });
  }
}
