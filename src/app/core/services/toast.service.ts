import {Injectable, signal} from '@angular/core';

export type ToastType = 'success' | 'info' | 'error' | 'warning';

export interface Toast {
  id: number;
  message: string;
  type: ToastType;
  leaving: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  /** Reactive list of active toasts */
  toasts = signal<Toast[]>([]);
  private nextId = 0;

  success(message: string): void {
    this.show(message, 'success');
  }

  info(message: string): void {
    this.show(message, 'info');
  }

  error(message: string): void {
    this.show(message, 'error');
  }

  warning(message: string): void {
    this.show(message, 'warning');
  }

  dismiss(id: number): void {
    // Mark as leaving to trigger exit animation
    this.toasts.update(list =>
      list.map(t => t.id === id ? {...t, leaving: true} : t)
    );

    // Remove from DOM after animation completes (400ms)
    setTimeout(() => {
      this.toasts.update(list => list.filter(t => t.id !== id));
    }, 400);
  }

  private show(message: string, type: ToastType): void {
    const id = this.nextId++;
    const toast: Toast = {id, message, type, leaving: false};

    this.toasts.update(list => [...list, toast]);

    // Start exit animation after 3 seconds, then remove
    setTimeout(() => this.dismiss(id), 3000);
  }
}
