import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {
  isButtonVisible = true;

  constructor(public router: Router) {}

  ngOnInit(): void {}

  login(): void {
    this.isButtonVisible = false;
    this.router.navigate(['/login']);
  }
}
