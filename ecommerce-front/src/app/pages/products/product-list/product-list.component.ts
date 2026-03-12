import { Component, OnInit } from '@angular/core';
import { Category, Product } from '../../../models/product.model';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-list',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.scss'
})
export class ProductListComponent implements OnInit{

  products: Product[] = [];
  categories: Category[] = [];
  searchTerm = '';
  selectedCategory: number | null = null;
  loading = false;
  addingToCart: number | null = null;

  constructor(
    private productService: ProductService,
    private cartService: CartService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getAll().subscribe({
      next: products => {
        this.products = products;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  loadCategories(): void {
    this.productService.getCategories().subscribe({
      next: categories => this.categories = categories
    });
  }

  onSearch(): void {
    if (this.searchTerm.trim()) {
      this.loading = true;
      this.productService.search(this.searchTerm).subscribe({
        next: products => {
          this.products = products;
          this.loading = false;
        },
        error: () => this.loading = false
      });
    } else {
      this.loadProducts();
    }
  }

  onCategoryChange(): void {
    if (this.selectedCategory) {
      this.loading = true;
      this.productService.getByCategory(this.selectedCategory).subscribe({
        next: products => {
          this.products = products;
          this.loading = false;
        },
        error: () => this.loading = false
      });
    } else {
      this.loadProducts();
    }
  }

  addToCart(product: Product): void {
    if (!this.authService.getToken()) {
      this.router.navigate(['/login']);
      return;
    }
    this.addingToCart = product.id;
    this.cartService.addItem(product.id, 1).subscribe({
      next: () => this.addingToCart = null,
      error: () => this.addingToCart = null
    });
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedCategory = null;
    this.loadProducts();
  }
}
