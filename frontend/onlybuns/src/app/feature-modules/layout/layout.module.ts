import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './navbar/navbar/navbar.component';
import { LoginComponent } from '../login/login.component';

@NgModule({
  declarations: [NavbarComponent, LoginComponent],
  imports: [CommonModule],
  exports: [NavbarComponent],
})
export class LayoutModule {}
