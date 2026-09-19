import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {Observable, tap} from 'rxjs';
import {ToastService} from './toast.service';

export interface LoginRequest {
  email: string;
  password?: string; // Optional if we are doing some other flow, but normally required
}

export interface AuthResponse {
  refreshToken: string;
  role: string;
  userId: number;
  expiresIn: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Use a signal to hold the user's role/auth status
  currentUserRole = signal<string | null>(null);
  currentUserId = signal<number | null>(null);
  private http = inject(HttpClient);
  private router = inject(Router);
  private toast = inject(ToastService);

  constructor() {
    // Re-hydrate state from local storage on startup
    const savedRole = localStorage.getItem('medicare-role');
    const savedUserId = localStorage.getItem('medicare-userId');
    if (savedRole) {
      this.currentUserRole.set(savedRole);
    }
    if (savedUserId) {
      this.currentUserId.set(Number(savedUserId));
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/v1/auth/login', credentials).pipe(
      tap((response: any) => {
        // The API returns ApiResponse<AuthResponse> so we might need to access response.data depending on how login is implemented.
        // Assuming response is directly AuthResponse based on previous code.
        const authData = response.data || response;
        this.currentUserRole.set(authData.role);
        this.currentUserId.set(authData.userId);
        localStorage.setItem('medicare-role', authData.role);
        if (authData.userId) {
          localStorage.setItem('medicare-userId', authData.userId.toString());
        }
        console.log(authData);
        let roleDisplay = authData.role.replace('ROLE_', '').replace('_', ' ');
        this.toast.success(`Successfully logged in as ${roleDisplay}`);
      })
    );
  }

  logout(): void {
    this.http.post('/api/v1/auth/logout', {}).subscribe({
      next: () => {
        this.clearAuth();
        this.toast.info('You have been logged out.');
      },
      error: () => {
        // Even if the server fails, clear local state
        this.clearAuth();
        this.toast.info('You have been logged out.');
      }
    });
  }

  isAuthenticated(): boolean {
    return this.currentUserRole() !== null;
  }

  private clearAuth() {
    this.currentUserRole.set(null);
    this.currentUserId.set(null);
    localStorage.removeItem('medicare-role');
    localStorage.removeItem('medicare-userId');
    this.router.navigate(['/login']);
  }
}
