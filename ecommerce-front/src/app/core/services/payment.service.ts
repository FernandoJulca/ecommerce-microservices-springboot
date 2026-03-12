import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Payment, PaymentRequest } from '../../models/payment.model';
import { environment } from '../../../enviroments/enviroment';

@Injectable({ providedIn: 'root' })
export class PaymentService {

  private apiUrl = `${environment.apiUrl}/payment-service/payments`;

  constructor(private http: HttpClient) {}

  processPayment(data: PaymentRequest): Observable<Payment> {
    return this.http.post<Payment>(this.apiUrl, data);
  }

  getMyPayments(): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.apiUrl}/my-payments`);
  }

  getPaymentByOrder(orderId: number): Observable<Payment> {
    return this.http.get<Payment>(`${this.apiUrl}/order/${orderId}`);
  }
}