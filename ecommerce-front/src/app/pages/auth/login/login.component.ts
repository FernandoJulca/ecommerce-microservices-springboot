import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  email = "";
  password = "";
  loading = false;
  errorMessage = "";

  constructor(
    private authService: AuthService,
    private router: Router
  ){}

  onLogin(): void{
    if(!this.email || !this.password){
      this.errorMessage = 'Por favor completa todos los campos'
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login({email: this.email, password: this.password}).subscribe({
      next: (res) => {
        this.loading = false;
        //Redirecciona segun el rol
        if(res.role === 'ADMIN'){
          this.router.navigate(['/admin/products']);
        } else {
          this.router.navigate(['/products']);
        }
      },
      error: (err) => {
        this.loading = false;
        if(err.status === 401){
          this.errorMessage = 'Email o contraseña incorrectos'
        } else {
          this.errorMessage = 'Error al iniciar sesión, intenta de nuevo'
        }
      }
    });
  }
}
