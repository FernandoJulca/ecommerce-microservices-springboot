import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { CartService } from '../../../core/services/cart.service';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss',
})
export class NavbarComponent implements OnInit {
  isLoggedIn = false;
  isAdmin = false;
  cartCount = 0;
  userEmail = '';

  constructor(
    private authService: AuthService,
    private cartService: CartService,
  ) {}

  ngOnInit(): void {
    this.authService.isLoggedIn$.subscribe((loggedIn) => {
      this.isLoggedIn = loggedIn;
      this.isAdmin = this.authService.isAdmin();
      this.userEmail = this.authService.getEmail() || '';
    });

    this.cartService.cartCount$.subscribe((count) => {
      this.cartCount = count;
    });
  }

  logout(): void {
    this.authService.logout();
  }
}
