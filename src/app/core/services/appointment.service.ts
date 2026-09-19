import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface Appointment {
  appointmentId: number;
  appointmentDate: string;
  appointmentTime: string;
  status: string;
  treatment: string;
  notes: string;
  doctorName?: string;
  patientName?: string;
  departmentName?: string;
}

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private http = inject(HttpClient);

  getPatientAppointments(userId: number, page = 0, size = 10): Observable<any> {
    return this.http.get<any>(`/api/v1/appointments/patient/${userId}?page=${page}&size=${size}`);
  }

  getDoctorAppointments(userId: number, page = 0, size = 10, date?: string): Observable<any> {
    let url = `/api/v1/appointments/doctor/${userId}?page=${page}&size=${size}`;
    if (date) {
      url += `&date=${date}`;
    }
    return this.http.get<any>(url);
  }
}
