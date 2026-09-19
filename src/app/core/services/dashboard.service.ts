import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

export interface DashboardKPIs {
  totalHospitals: number;
  totalDoctors: number;
  totalPatients: number;
  totalStaff: number;
  totalAppointmentsToday: number;
  totalDepartments: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private http = inject(HttpClient);

  getKPIs(): Observable<DashboardKPIs> {
    return this.http.get<DashboardKPIs>('/api/v1/admin/dashboard/kpis');
  }
}
