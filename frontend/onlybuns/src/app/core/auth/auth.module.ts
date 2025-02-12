import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RegistrationComponent } from '../../feature-modules/layout/registration/registration.component';
import { LoginComponent } from '../../feature-modules/layout/login/login.component';

@NgModule({
  declarations: [LoginComponent, RegistrationComponent],
  imports: [CommonModule, ReactiveFormsModule],
})
export class AuthModule {}
