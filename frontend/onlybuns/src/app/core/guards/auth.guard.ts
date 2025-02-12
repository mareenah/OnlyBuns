import { Router, UrlTree } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { Observable } from 'rxjs';

export class AuthGuard {
  constructor(private router: Router, private authService: AuthService) {}
  canActivate(): boolean | UrlTree | Observable<boolean | UrlTree> {
    let user = this.authService.user$.getValue();
    if (user.username === '') {
      this.router.createUrlTree(['/login']);
      return false;
    }
    return true;
  }
}
