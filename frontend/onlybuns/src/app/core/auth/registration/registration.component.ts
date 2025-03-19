import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  AbstractControlOptions,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { AuthService } from '../auth.service';
import { Registration } from '../../models/registration.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent implements OnInit {
  registerForm!: FormGroup;
  emailRegex: RegExp =
    /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  passwordRegex: RegExp =
    /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[!?@#$%^&*><:;,.()]).{8,}$/;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.registerForm = this.fb.group(
      {
        email: [
          '',
          {
            validators: [
              Validators.required,
              Validators.pattern(this.emailRegex),
            ],
          },
        ],
        username: [
          '',
          {
            validators: [
              Validators.required,
              Validators.minLength(4),
              Validators.maxLength(30),
              Validators.pattern(/^[a-zA-Z][a-zA-Z0-9._]*[a-zA-Z0-9]$/),
            ],
          },
        ],
        password: [
          '',
          {
            validators: [
              Validators.required,
              Validators.pattern(this.passwordRegex),
            ],
          },
        ],
        confirmPassword: ['', [Validators.required]],
        name: [
          '',
          {
            validators: [
              Validators.required,
              Validators.minLength(2),
              Validators.maxLength(50),
              Validators.pattern(/^[A-Za-zÀ-ÖØ-öø-ÿ' -]+$/),
            ],
          },
        ],
        lastname: [
          '',
          {
            validators: [
              Validators.required,
              Validators.minLength(2),
              Validators.maxLength(50),
              Validators.pattern(/^[A-Za-zÀ-ÖØ-öø-ÿ' -]+$/),
            ],
          },
        ],
        address: [
          '',
          {
            validators: [
              Validators.required,
              Validators.minLength(5),
              Validators.maxLength(100),
              Validators.pattern(/^[A-Za-z0-9À-ÖØ-öø-ÿ,.\-\/\s]+$/),
            ],
          },
        ],
      },
      {
        validators: this.matchPasswords('password', 'confirmPassword'),
      } as AbstractControlOptions
    );
  }

  matchPasswords(password: string, confirmPassword: string) {
    return (control: AbstractControl): ValidationErrors | null => {
      console.log('matchPassword');
      const passControl = control.get(password);
      const confirmPassControl = control.get(confirmPassword);

      if (!passControl || !confirmPassControl) {
        return null; // if controls are not found, don't validate
      }

      if (passControl.value !== confirmPassControl.value) {
        confirmPassControl.setErrors({ mismatch: true });
      } else {
        confirmPassControl.setErrors(null);
      }

      return null;
    };
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      console.log('Form submitted:', this.registerForm.value);
      const registration: Registration = {
        email: this.registerForm.value.email,
        username: this.registerForm.value.username,
        password: this.registerForm.value.password,
        confirmPassword: this.registerForm.value.confirmPassword,
        name: this.registerForm.value.name,
        lastname: this.registerForm.value.lastname,
        address: this.registerForm.value.address,
      };
      this.authService.register(registration).subscribe({
        next: (response) => {
          alert(
            'You have signed up successfully!\nVerify your account and login.'
          );
          this.router.navigate(['']);
        },
        error: (error) => {
          if (error.status === 409) {
            alert(error.error);
          } else if (error.status === 500) {
            alert('An unexpected error occurred. Please try again later.');
          } else {
            alert('Unknown error occurred.');
          }
        },
      });
    } else {
      this.registerForm.markAllAsTouched();
    }
  }

  get confirmPasswordControl() {
    return this.registerForm.get('confirmPassword');
  }

  get emailControl() {
    return this.registerForm.get('email');
  }

  get isEmailInvalid(): boolean {
    return this.emailControl?.errors?.['pattern'];
  }
}
