import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Cart } from '../../models/cart.model';
import { environment } from '../../../enviroments/enviroment';

@Injectable({ providedIn: 'root' })
export class CartService {

  private apiUrl = `${environment.apiUrl}/cart-service/cart`;
  private cartCount = new BehaviorSubject<number>(0);
  cartCount$ = this.cartCount.asObservable();

  constructor(private http: HttpClient) {}

  getCart(): Observable<Cart> {
    return this.http.get<Cart>(this.apiUrl).pipe(
      tap(cart => this.cartCount.next(cart.totalItems))
    );
  }

  addItem(productId: number, quantity: number): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/items`, { productId, quantity }).pipe(
      tap(cart => this.cartCount.next(cart.totalItems))
    );
  }

  removeItem(itemId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/items/${itemId}`).pipe(
      tap(cart => this.cartCount.next(cart.totalItems))
    );
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(this.apiUrl);
  }
}