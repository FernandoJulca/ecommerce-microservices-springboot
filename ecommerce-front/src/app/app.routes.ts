import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { AdminGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/products', pathMatch: 'full' },

  // Públicas
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/auth/login/login.component').then(
        (m) => m.LoginComponent,
      ),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./pages/auth/register/register.component').then(
        (m) => m.RegisterComponent,
      ),
  },
  {
    path: 'products',
    loadComponent: () =>
      import('./pages/products/product-list/product-list.component').then(
        (m) => m.ProductListComponent,
      ),
  },
  {
    path: 'products/:id',
    loadComponent: () =>
      import('./pages/products/product-detail/product-detail.component').then(
        (m) => m.ProductDetailComponent,
      ),
  },

  // Usuario autenticado
  {
    path: 'cart',
    canActivate: [AuthGuard],
    loadComponent: () =>
      import('./pages/cart/cart.component').then((m) => m.CartComponent),
  },
  {
    path: 'checkout',
    canActivate: [AuthGuard],
    loadComponent: () =>
      import('./pages/checkout/checkout.component').then(
        (m) => m.CheckoutComponent,
      ),
  },
  {
    path: 'orders',
    canActivate: [AuthGuard],
    loadComponent: () =>
      import('./pages/orders/orders.component').then((m) => m.OrdersComponent),
  },

  // Solo ADMIN
  {
    path: 'admin/products',
    canActivate: [AuthGuard, AdminGuard],
    loadComponent: () =>
      import('./pages/admin/product-management/product-management.component').then(
        (m) => m.ProductManagementComponent,
      ),
  },
  {
    path: 'admin/orders',
    canActivate: [AuthGuard, AdminGuard],
    loadComponent: () =>
      import('./pages/admin/order-management/order-management.component').then(
        (m) => m.OrderManagementComponent,
      ),
  },

  { path: '**', redirectTo: '/products' },
];
