import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {
  isButtonVisible: any;

  constructor(public router: Router) {}

  ngOnInit(): void {
    console.log('ngOnInit in navbar');
    this.isButtonVisible = true;
  }

  login(): void {
    console.log('login in navbar');
    this.isButtonVisible = false;
    this.router.navigate(['/login']);
  }
}
