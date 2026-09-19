import { Injectable, signal } from '@angular/core';

export type Theme = 'light' | 'dark' | 'system';
export type ColorTheme = 'ocean' | 'rose' | 'sapphire' | 'sky' | 'emerald' | 'violet' | 'sunset';

export interface ColorThemeOption {
  id: ColorTheme;
  label: string;
  swatch: string; // CSS color for preview dot
}

export const COLOR_THEMES: ColorThemeOption[] = [
  { id: 'ocean',    label: 'Ocean',    swatch: '#06b6d4' },
  { id: 'rose',     label: 'Rose',     swatch: '#f43f5e' },
  { id: 'sapphire', label: 'Sapphire', swatch: '#6366f1' },
  { id: 'sky',      label: 'Sky',      swatch: '#0ea5e9' },
  { id: 'emerald',  label: 'Emerald',  swatch: '#10b981' },
  { id: 'violet',   label: 'Violet',   swatch: '#8b5cf6' },
  { id: 'sunset',   label: 'Sunset',   swatch: '#f97316' },
];

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly THEME_KEY = 'medicare-theme';
  private readonly COLOR_THEME_KEY = 'medicare-color-theme';

  // Signal to store current theme preference
  currentTheme = signal<Theme>('system');

  // Signal to store resolved theme (always 'light' or 'dark')
  resolvedTheme = signal<'light' | 'dark'>('light');

  // Signal for color theme
  currentColorTheme = signal<ColorTheme>('ocean');

  // Available color themes
  readonly colorThemes = COLOR_THEMES;

  constructor() {
    this.initTheme();
    this.initColorTheme();

    // Listen for system theme changes
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
      if (this.currentTheme() === 'system') {
        this.applyTheme('system');
      }
    });
  }

  private initTheme() {
    const savedTheme = localStorage.getItem(this.THEME_KEY) as Theme;
    if (savedTheme && ['light', 'dark', 'system'].includes(savedTheme)) {
      this.setTheme(savedTheme);
    } else {
      this.setTheme('system');
    }
  }

  private initColorTheme() {
    const saved = localStorage.getItem(this.COLOR_THEME_KEY) as ColorTheme;
    const validIds = COLOR_THEMES.map(t => t.id);
    if (saved && validIds.includes(saved)) {
      this.setColorTheme(saved);
    } else {
      this.setColorTheme('ocean');
    }
  }

  setTheme(theme: Theme) {
    this.currentTheme.set(theme);
    localStorage.setItem(this.THEME_KEY, theme);
    this.applyTheme(theme);
  }

  setColorTheme(colorTheme: ColorTheme) {
    this.currentColorTheme.set(colorTheme);
    localStorage.setItem(this.COLOR_THEME_KEY, colorTheme);
    document.documentElement.setAttribute('data-color-theme', colorTheme);
  }

  private applyTheme(theme: Theme) {
    const isDark = theme === 'dark' ||
                  (theme === 'system' && window.matchMedia('(prefers-color-scheme: dark)').matches);

    this.resolvedTheme.set(isDark ? 'dark' : 'light');

    if (isDark) {
      document.documentElement.classList.add('dark');
      document.documentElement.classList.remove('light');
    } else {
      document.documentElement.classList.add('light');
      document.documentElement.classList.remove('dark');
    }
  }
}
