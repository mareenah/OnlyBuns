import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './navbar/navbar/navbar.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthModule } from 'src/app/core/auth/auth.module';
import { RouterModule } from '@angular/router';
@NgModule({
  declarations: [NavbarComponent],
  imports: [CommonModule, ReactiveFormsModule, AuthModule, RouterModule],
  exports: [NavbarComponent],
})
export class LayoutModule {}
