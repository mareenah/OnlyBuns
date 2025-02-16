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
  emailRegex: RegExp =
    /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
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
          this.router.navigate(['']);
        },
        error: (error) => {
          alert(error);
        },
      });

      console.warn('Submitted: ', this.loginForm.value);
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
