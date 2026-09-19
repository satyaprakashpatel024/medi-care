import { Component, inject, OnInit, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService, DashboardKPIs } from '../../core/services/dashboard.service';
import { AuthService } from '../../core/services/auth.service';
import { Observable } from 'rxjs';
import { DoctorDashboardComponent } from './doctor-dashboard/doctor-dashboard.component';
import { PatientDashboardComponent } from './patient-dashboard/patient-dashboard.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, DoctorDashboardComponent, PatientDashboardComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  authService = inject(AuthService);
  private dashboardService = inject(DashboardService);

  kpis$!: Observable<DashboardKPIs> | null;

  ngOnInit() {
    const role = this.authService.currentUserRole();
    if (role === 'SUPER_ADMIN' || role === 'HOSPITAL_ADMIN') {
      this.kpis$ = this.dashboardService.getKPIs();
    } else {
      this.kpis$ = null;
    }
  }
}
