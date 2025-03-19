import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RegistrationComponent } from './registration/registration.component';
import { LoginComponent } from './login/login.component';
import { CheckEmailComponent } from './check-email/check-email.component';
import { RouterModule } from '@angular/router';
@NgModule({
  declarations: [LoginComponent, RegistrationComponent, CheckEmailComponent],
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
})
export class AuthModule {}
