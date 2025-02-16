import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from './navbar/navbar/navbar.component';
import { ReactiveFormsModule } from '@angular/forms';
@NgModule({
  declarations: [NavbarComponent],
  imports: [CommonModule, ReactiveFormsModule],
  exports: [NavbarComponent],
})
export class LayoutModule {}
