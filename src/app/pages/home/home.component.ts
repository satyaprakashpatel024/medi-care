import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ThemeService, Theme, ColorTheme } from '../../core/services/theme.service';
import { LucideAngularModule, HeartPulse, ShieldCheck, Clock, ArrowRight } from 'lucide-angular';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, LucideAngularModule],
  templateUrl: './home.component.html'
})
export class HomeComponent {
  themeService = inject(ThemeService);
  
  // Expose icons to template
  readonly HeartPulse = HeartPulse;
  readonly ShieldCheck = ShieldCheck;
  readonly Clock = Clock;
  readonly ArrowRight = ArrowRight;

  onThemeChange(event: Event) {
    const select = event.target as HTMLSelectElement;
    this.themeService.setTheme(select.value as Theme);
  }

  onColorThemeChange(colorTheme: ColorTheme) {
    this.themeService.setColorTheme(colorTheme);
  }
}
