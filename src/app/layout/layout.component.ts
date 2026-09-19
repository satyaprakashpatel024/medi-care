import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {AuthService} from '../core/services/auth.service';
import {ColorTheme, Theme, ThemeService} from '../core/services/theme.service';
import {Calendar, FileText, LayoutDashboard, LogOut, LucideAngularModule, Settings, Users} from 'lucide-angular';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive, LucideAngularModule],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.css'
})
export class LayoutComponent {
  authService = inject(AuthService);
  themeService = inject(ThemeService);

  readonly LayoutDashboard = LayoutDashboard;
  readonly Calendar = Calendar;
  readonly Users = Users;
  readonly FileText = FileText;
  readonly Settings = Settings;
  readonly LogOut = LogOut;

  onThemeChange(event: Event) {
    const select = event.target as HTMLSelectElement;
    this.themeService.setTheme(select.value as Theme);
  }

  onColorThemeChange(colorTheme: ColorTheme) {
    this.themeService.setColorTheme(colorTheme);
  }

  logout() {
    this.authService.logout();
  }
}
