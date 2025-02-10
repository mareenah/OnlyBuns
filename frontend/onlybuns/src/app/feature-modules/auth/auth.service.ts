import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Login } from 'src/app/models/login.model';
import { AuthenticationResponse } from 'src/app/models/authenticaion-response.model';
import { TokenStorage } from 'src/app/services/token.service';
import { JwtHelperService } from '@auth0/angular-jwt';
import { User } from '../../models/user.model';

@Injectable()
export class AuthService {
  user$ = new BehaviorSubject<User>({
    username: '',
    id: 0,
    role: '',
  });

  constructor(private http: HttpClient, private tokenStorage: TokenStorage) {}

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
}
