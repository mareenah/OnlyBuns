import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  AbstractControlOptions,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  Validators,
} from '@angular/forms';

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

  constructor(private fb: FormBuilder) {}

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
    } else {
      this.registerForm.markAllAsTouched(); // Mark all fields as touched to show errors
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
