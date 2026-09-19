import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Appointment, AppointmentService} from '../../../core/services/appointment.service';
import {AuthService} from '../../../core/services/auth.service';

@Component({
  selector: 'app-doctor-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './doctor-dashboard.component.html'
})
export class DoctorDashboardComponent implements OnInit {
  appointments: Appointment[] = [];
  totalAppointments = 0;
  loading = true;
  private appointmentService = inject(AppointmentService);
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit() {
    const userId = this.authService.currentUserId();
    if (userId) {
      const today = new Date().toISOString().split('T')[0];
      this.appointmentService.getDoctorAppointments(userId, 0, 10, today).subscribe({
        next: (res) => {
          this.appointments = res?.data?.content || [];
          this.totalAppointments = res?.data?.totalElements || this.appointments.length;
          this.loading = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error fetching appointments', err);
          this.loading = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      this.loading = false;
      this.cdr.detectChanges();
    }
  }
}

