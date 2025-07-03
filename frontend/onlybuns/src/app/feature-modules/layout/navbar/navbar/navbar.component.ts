import { Component, ElementRef, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/auth/auth.service';
import { User } from 'src/app/core/models/user.model';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {
  showButton = false;
  user: User | undefined;
  dropdownOpen = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private elementRef: ElementRef
  ) {}

  ngOnInit(): void {
    this.router.events.subscribe(() => {
      this.showButton = this.router.url === '/';
    });
    this.authService.user$.subscribe((user) => {
      this.user = user;
      console.log(
        'Navbar\n' +
          'username: ' +
          user.username +
          ' role: ' +
          user.role +
          ' id: ' +
          user.id
      );
    });
  }

  hasLoggedIn(): boolean {
    if (this.user?.id === '') {
      return false;
    }
    return true;
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logout() {
    this.authService.logout();
    this.dropdownOpen = false;
  }

  logo() {
    this.router.navigate(['/']);
  }

  toggleMenu() {
    this.dropdownOpen = !this.dropdownOpen;
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!this.elementRef.nativeElement.contains(target)) {
      this.dropdownOpen = false;
    }
  }
}
