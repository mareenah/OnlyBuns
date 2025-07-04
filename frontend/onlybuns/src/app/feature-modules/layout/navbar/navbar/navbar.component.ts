import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {
  showButton: boolean = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.router.events.subscribe(() => {
      this.showButton = this.router.url === '/';
    });
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logo() {
    this.router.navigate(['/']);
  }
}
