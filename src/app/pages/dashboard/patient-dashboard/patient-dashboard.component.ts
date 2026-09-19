import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Appointment, AppointmentService} from '../../../core/services/appointment.service';
import {AuthService} from '../../../core/services/auth.service';
import {
  CalendarClock,
  CalendarDays,
  CalendarX2,
  Clock,
  FileText,
  History,
  LucideAngularModule,
  Stethoscope,
  UserRound,
  X
} from 'lucide-angular';

@Component({
  selector: 'app-patient-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, LucideAngularModule],
  templateUrl: './patient-dashboard.component.html'
})
export class PatientDashboardComponent implements OnInit {
  // Lucide icons
  readonly UserRound = UserRound;
  readonly CalendarDays = CalendarDays;
  readonly CalendarClock = CalendarClock;
  readonly CalendarX2 = CalendarX2;
  readonly Clock = Clock;
  readonly History = History;
  readonly X = X;
  readonly Stethoscope = Stethoscope;
  readonly FileText = FileText;
  appointments: Appointment[] = [];
  filteredAppointments: Appointment[] = [];
  loading = true;
  // Filter state
  activeFilter: 'all' | 'upcoming' | 'past' = 'all';
  startDate = '';
  endDate = '';
  // Modal state
  selectedAppointment: Appointment | null = null;
  private appointmentService = inject(AppointmentService);
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit() {
    const userId = this.authService.currentUserId();
    if (userId) {
      this.appointmentService.getPatientAppointments(userId).subscribe({
        next: (res) => {
          this.appointments = res?.data?.content || [];
          this.applyFilters();
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

  setFilter(filter: 'all' | 'upcoming' | 'past') {
    this.activeFilter = filter;
    this.applyFilters();
  }

  applyDateRange() {
    this.applyFilters();
  }

  clearDateRange() {
    this.startDate = '';
    this.endDate = '';
    this.applyFilters();
  }

  applyFilters() {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    let result = [...this.appointments];

    // Apply tab filter
    if (this.activeFilter === 'upcoming') {
      result = result.filter(apt => {
        const aptDate = new Date(apt.appointmentDate);
        aptDate.setHours(0, 0, 0, 0);
        return aptDate >= today;
      });
    } else if (this.activeFilter === 'past') {
      result = result.filter(apt => {
        const aptDate = new Date(apt.appointmentDate);
        aptDate.setHours(0, 0, 0, 0);
        return aptDate < today;
      });
    }

    // Apply date range filter
    if (this.startDate) {
      const start = new Date(this.startDate);
      start.setHours(0, 0, 0, 0);
      result = result.filter(apt => {
        const aptDate = new Date(apt.appointmentDate);
        aptDate.setHours(0, 0, 0, 0);
        return aptDate >= start;
      });
    }
    if (this.endDate) {
      const end = new Date(this.endDate);
      end.setHours(23, 59, 59, 999);
      result = result.filter(apt => {
        const aptDate = new Date(apt.appointmentDate);
        return aptDate <= end;
      });
    }

    this.filteredAppointments = result;
    this.cdr.detectChanges();
  }

  getDoctorInitials(name?: string): string {
    if (!name) return '?';
    return name.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase();
  }

  openDetail(apt: Appointment) {
    this.selectedAppointment = apt;
  }

  closeDetail() {
    this.selectedAppointment = null;
  }
}
