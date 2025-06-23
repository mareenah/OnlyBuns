import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';
import { Login } from 'src/app/core/models/login.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  emailRegex: RegExp = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
  passwordRegex: RegExp =
    /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[!?@#$%^&*><:;,.()]).{8,}$/;

  loginForm = new FormGroup({
    email: new FormControl('', {
      validators: [Validators.required, Validators.pattern(this.emailRegex)],
    }),
    password: new FormControl('', {
      validators: [Validators.required, Validators.pattern(this.passwordRegex)],
    }),
  });

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    const login: Login = {
      email: this.loginForm.value.email || '',
      password: this.loginForm.value.password || '',
    };

    if (this.loginForm.valid) {
      this.authService.login(login).subscribe({
        next: () => {
          alert('You have logged in successfully!');
          this.router.navigate(['']);
        },
        error: (error) => {
          switch (error.status) {
            case 400:
            case 401:
            case 403:
            case 429:
              alert(error.error.message);
              break;
            default:
              alert('Login failed due to an unexpected error.');
              break;
          }
        },
      });
    } else {
      console.warn('Form is invalid', this.loginForm.errors);
    }
  }

  get passwordFormField() {
    return this.loginForm.get('password');
  }

  get isPasswordInvalid(): boolean {
    return this.passwordFormField?.errors?.['pattern'];
  }

  get emailFormField() {
    return this.loginForm.get('email');
  }

  get isEmailInvalid(): boolean {
    return this.emailFormField?.errors?.['pattern'];
  }
}
