import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Login } from 'src/app/core/models/login.model';
import { AuthenticationResponse } from 'src/app/core/models/authenticaion-response.model';
import { TokenStorage } from 'src/app/core/auth/jwt/token.service';
import { JwtHelperService } from '@auth0/angular-jwt';
import { User } from '../models/user.model';
import { Registration } from 'src/app/core/models/registration.model';
import { environment } from 'src/app/core/models/constants';
import { RegistrationResponse } from 'src/app/core/models/registration-response.model';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  user$ = new BehaviorSubject<User>({
    username: '',
    id: 0,
    role: '',
  });

  constructor(
    private http: HttpClient,
    private tokenStorage: TokenStorage,
    private router: Router
  ) {}

  login(login: Login): Observable<AuthenticationResponse> {
    return this.http
      .post<AuthenticationResponse>(
        'http://localhost:4200/' + 'auth/login',
        login
      )
      .pipe(
        tap((authenticationResponse) => {
          this.tokenStorage.saveAccessToken(authenticationResponse.accessToken);
          this.setUser();
        })
      );
  }

  private setUser(): void {
    const jwtHelperService = new JwtHelperService();
    const accessToken = this.tokenStorage.getAccessToken() || '';
    const user: User = {
      id: +jwtHelperService.decodeToken(accessToken).id,
      username: jwtHelperService.decodeToken(accessToken).username,
      role: jwtHelperService.decodeToken(accessToken).role,
    };

    this.user$.next(user);
  }

  register(registration: Registration): Observable<RegistrationResponse> {
    return this.http.post<RegistrationResponse>(
      environment.apiHost + 'auth/register',
      registration
    );
  }

  logout(): void {
    this.tokenStorage.clear();
    this.router.navigate(['']);
    this.user$.next({ id: 0, username: '', role: '' });
  }
}
