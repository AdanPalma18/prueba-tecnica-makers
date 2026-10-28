import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { tap } from 'rxjs';
import { environment } from '../environments/environment-development';
import { AuthResponse } from '../models/auth-response.model';

interface LoginCredentials {
  email: string;
  password: string;
}

export interface RegisterResponse {
  success: boolean;
  message: string;
  data: {
    id: number;
    email: string;
  };
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenState = signal(sessionStorage.getItem('accessToken'));
  private readonly rolesState = signal(
    (sessionStorage.getItem('userRoles') ?? '').split(',').filter(Boolean),
  );
  private readonly emailState = signal(sessionStorage.getItem('userEmail') ?? '');

  readonly email = this.emailState.asReadonly();
  readonly roles = this.rolesState.asReadonly();
  readonly isAuthenticated = computed(() => Boolean(this.tokenState()));
  readonly isAdmin = computed(() => this.rolesState().includes('ROLE_ADMIN'));

  login(credentials: LoginCredentials) {
    return this.http
      .post<AuthResponse>(`${environment.apiUrl}/auth/login`, credentials)
      .pipe(tap((response) => this.saveSession(credentials.email, response)));
  }

  register(credentials: LoginCredentials) {
    return this.http.post<RegisterResponse>(`${environment.apiUrl}/auth/register`, credentials);
  }

  getToken(): string | null {
    return this.tokenState();
  }

  logout(): void {
    sessionStorage.removeItem('accessToken');
    sessionStorage.removeItem('userRoles');
    sessionStorage.removeItem('userEmail');
    this.tokenState.set(null);
    this.rolesState.set([]);
    this.emailState.set('');
  }

  private saveSession(email: string, response: AuthResponse): void {
    if (!response.accessToken || !Array.isArray(response.roles)) {
      throw new Error('La respuesta de autenticación del servidor no es válida.');
    }

    sessionStorage.setItem('accessToken', response.accessToken);
    sessionStorage.setItem('userRoles', response.roles.join(','));
    sessionStorage.setItem('userEmail', email);
    this.tokenState.set(response.accessToken);
    this.rolesState.set(response.roles);
    this.emailState.set(email);
  }
}
