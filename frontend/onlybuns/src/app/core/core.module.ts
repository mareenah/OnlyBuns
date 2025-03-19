import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthModule } from './auth/auth.module';
import { RouterModule } from '@angular/router';
@NgModule({
  declarations: [],
  imports: [CommonModule, RouterModule, AuthModule],
  providers: [],
  exports: [AuthModule],
})
export class CoreModule {}
