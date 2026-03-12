import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product, Category } from '../../models/product.model';
import { environment } from '../../../enviroments/enviroment';

@Injectable({ providedIn: 'root' })
export class ProductService {

  private apiUrl = `${environment.apiUrl}/product-service`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products`);
  }

  getById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/products/${id}`);
  }

  search(name: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products/search?name=${name}`);
  }

  getByCategory(categoryId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products/category/${categoryId}`);
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/categories`);
  }

  // ADMIN
  create(formData: FormData): Observable<Product> {
    return this.http.post<Product>(`${this.apiUrl}/products`, formData);
  }

  update(id: number, formData: FormData): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/products/${id}`, formData);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/products/${id}`);
  }
}