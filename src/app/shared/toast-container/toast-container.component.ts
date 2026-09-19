import { Component, inject } from '@angular/core';
import { ToastService, Toast } from '../../core/services/toast.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  template: `
    <div class="toast-container">
      @for (toast of toastService.toasts(); track toast.id) {
        <div
          class="toast-item"
          [class.toast-success]="toast.type === 'success'"
          [class.toast-info]="toast.type === 'info'"
          [class.toast-error]="toast.type === 'error'"
          [class.toast-warning]="toast.type === 'warning'"
          [class.toast-leaving]="toast.leaving"
        >
          <span class="toast-icon">
            @switch (toast.type) {
              @case ('success') { ✓ }
              @case ('info') { ℹ }
              @case ('error') { ✕ }
              @case ('warning') { ⚠ }
            }
          </span>
          <span class="toast-message">{{ toast.message }}</span>
          <button class="toast-close" (click)="toastService.dismiss(toast.id)" aria-label="Close">×</button>
        </div>
      }
    </div>
  `,
  styles: [`
    .toast-container {
      position: fixed;
      top: 16px;
      right: 16px;
      z-index: 10000;
      display: flex;
      flex-direction: column;
      gap: 10px;
      pointer-events: none;
      max-width: 400px;
    }

    .toast-item {
      pointer-events: auto;
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 12px 16px;
      border-radius: 12px;
      color: #fff;
      font-size: 14px;
      font-weight: 500;
      font-family: 'Inter', 'Segoe UI', sans-serif;
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.25), 0 2px 8px rgba(0, 0, 0, 0.15);
      backdrop-filter: blur(12px);
      animation: toast-enter 0.4s cubic-bezier(0.21, 1.02, 0.73, 1) forwards;
      min-width: 280px;
    }

    .toast-leaving {
      animation: toast-leave 0.4s cubic-bezier(0.06, 0.71, 0.55, 1) forwards;
    }

    .toast-success {
      background: linear-gradient(135deg, #059669, #10b981);
      border: 1px solid rgba(255, 255, 255, 0.15);
    }

    .toast-info {
      background: linear-gradient(135deg, #0284c7, #0ea5e9);
      border: 1px solid rgba(255, 255, 255, 0.15);
    }

    .toast-error {
      background: linear-gradient(135deg, #dc2626, #ef4444);
      border: 1px solid rgba(255, 255, 255, 0.15);
    }

    .toast-warning {
      background: linear-gradient(135deg, #d97706, #f59e0b);
      border: 1px solid rgba(255, 255, 255, 0.15);
    }

    .toast-icon {
      flex-shrink: 0;
      width: 24px;
      height: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.2);
      font-size: 12px;
      font-weight: 700;
    }

    .toast-message {
      flex: 1;
      line-height: 1.4;
    }

    .toast-close {
      flex-shrink: 0;
      background: none;
      border: none;
      color: rgba(255, 255, 255, 0.7);
      font-size: 18px;
      cursor: pointer;
      padding: 0 2px;
      line-height: 1;
      transition: color 0.2s;
    }

    .toast-close:hover {
      color: #fff;
    }

    @keyframes toast-enter {
      0% {
        opacity: 0;
        transform: translateX(100%) scale(0.9);
      }
      100% {
        opacity: 1;
        transform: translateX(0) scale(1);
      }
    }

    @keyframes toast-leave {
      0% {
        opacity: 1;
        transform: translateX(0) scale(1);
      }
      100% {
        opacity: 0;
        transform: translateX(100%) scale(0.9);
      }
    }
  `]
})
export class ToastContainerComponent {
  toastService = inject(ToastService);
}
