import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../../core/services/product.service';
import { Category, Product } from '../../../models/product.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-product-management',
  imports: [CommonModule,FormsModule],
  templateUrl: './product-management.component.html',
  styleUrl: './product-management.component.scss'
})
export class ProductManagementComponent implements OnInit{

  products: Product[] = [];
  categories: Category[] = [];
  loading = false;
  saving = false;
  errorMessage = '';

  // Modal
  showModal = false;
  isEditing = false;

  // Formulario
  form = this.emptyForm();

  // Imagen
  selectedFile: File | null = null;
  imagePreview: string | null = null;

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadCategories();
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getAllAdmin().subscribe({
      next: products => {
        this.products = products;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  toggleActive(product: Product): void {
  const accion = product.active ? 'desactivar' : 'activar';
  if (!confirm(`¿Deseas ${accion} "${product.name}"?`)) return;

  this.productService.toggleActive(product.id).subscribe({
    next: () => this.loadProducts()
  });
}

  loadCategories(): void {
    this.productService.getCategories().subscribe({
      next: categories => this.categories = categories
    });
  }

  emptyForm() {
    return {
      id: null as number | null,
      name: '',
      description: '',
      price: null as number | null,
      stock: null as number | null,
      categoryId: null as number | null
    };
  }

  openCreate(): void {
    this.form = this.emptyForm();
    this.selectedFile = null;
    this.imagePreview = null;
    this.isEditing = false;
    this.errorMessage = '';
    this.showModal = true;
  }

  openEdit(product: Product): void {
    this.form = {
      id: product.id,
      name: product.name,
      description: product.description,
      price: product.price,
      stock: product.stock,
      categoryId: this.categories.find(c => c.name === product.categoryName)?.id || null
    };
    this.selectedFile = null;
    this.imagePreview = product.imageUrl || null;
    this.isEditing = true;
    this.errorMessage = '';
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = e => this.imagePreview = e.target?.result as string;
      reader.readAsDataURL(this.selectedFile);
    }
  }

  saveProduct(): void {
  if (!this.form.name || !this.form.price || !this.form.stock || !this.form.categoryId) {
    this.errorMessage = 'Por favor completa todos los campos obligatorios';
    return;
  }

  this.saving = true;
  this.errorMessage = '';

  const formData = new FormData();

 
  const productJson = JSON.stringify({
    name: this.form.name,
    description: this.form.description,
    price: this.form.price,
    stock: this.form.stock,
    categoryId: this.form.categoryId
  });

  formData.append('product', new Blob([productJson], { type: 'application/json' }));

  if (this.selectedFile) {
    formData.append('image', this.selectedFile);
  }

  const request$ = this.isEditing
    ? this.productService.update(this.form.id!, formData)
    : this.productService.create(formData);

  request$.subscribe({
    next: () => {
      this.saving = false;
      this.showModal = false;
      this.loadProducts();
    },
    error: () => {
      this.saving = false;
      this.errorMessage = 'Error al guardar el producto';
    }
  });
}

  deleteProduct(product: Product): void {
    if (!confirm(`¿Eliminar "${product.name}"?`)) return;

    this.productService.delete(product.id).subscribe({
      next: () => this.loadProducts()
    });
  }

  getCategoryName(categoryId: number): string {
    return this.categories.find(c => c.id === categoryId)?.name || '—';
  }
}
