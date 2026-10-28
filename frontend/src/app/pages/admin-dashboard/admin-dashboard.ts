import { HttpErrorResponse } from '@angular/common/http';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { Loan, LoanStatus } from '../../models/loan.model';
import { PageResponse } from '../../models/page-response.model';
import { AuthService } from '../../services/auth-service';
import { LoanService } from '../../services/loan-service';

@Component({
  selector: 'app-admin-dashboard',
  imports: [CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboard implements OnInit {
  private readonly loanService = inject(LoanService);
  private readonly router = inject(Router);

  readonly auth = inject(AuthService);
  readonly loans = signal<Loan[]>([]);
  readonly loading = signal(true);
  readonly page = signal(0);
  readonly pageSize = signal(5);
  readonly totalElements = signal(0);
  readonly totalPages = signal(0);
  readonly firstItem = signal(0);
  readonly lastItem = signal(0);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');
  readonly successStatus = signal<Extract<LoanStatus, 'APPROVED' | 'REJECTED'> | null>(null);
  readonly updatingIds = signal(new Set<number>());
  readonly pendingCount = computed(() => this.loans().filter((loan) => loan.status === 'PENDING').length);
  readonly approvedCount = computed(() => this.loans().filter((loan) => loan.status === 'APPROVED').length);

  ngOnInit(): void {
    this.loadLoans();
  }

  loadLoans(pageNumber = this.page()): void {
    this.loading.set(true);
    this.errorMessage.set('');
    this.loanService
      .getAllLoans(pageNumber, this.pageSize())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (result: PageResponse<Loan>) => {
          this.loans.set(result.content);
          this.page.set(result.number);
          this.totalElements.set(result.totalElements);
          this.totalPages.set(result.totalPages);
          this.firstItem.set(result.totalElements === 0 ? 0 : result.number * result.size + 1);
          this.lastItem.set(result.number * result.size + result.numberOfElements);
        },
        error: (error: unknown) => this.errorMessage.set(this.describeError(error)),
      });
  }

  changePage(pageNumber: number): void {
    if (pageNumber >= 0 && pageNumber < this.totalPages()) {
      this.loadLoans(pageNumber);
    }
  }

  changePageSize(event: Event): void {
    const target = event.target;
    if (target instanceof HTMLSelectElement) {
      const newSize = Number(target.value);
      if ([5, 10, 20].includes(newSize)) {
        this.pageSize.set(newSize);
        this.loadLoans(0);
      }
    }
  }

  updateStatus(loan: Loan, status: Extract<LoanStatus, 'APPROVED' | 'REJECTED'>): void {
    this.errorMessage.set('');
    this.successMessage.set('');
    this.successStatus.set(null);
    this.updatingIds.update((ids) => new Set(ids).add(loan.id));
    this.loanService
      .updateStatus(loan.id, status)
      .pipe(
        finalize(() =>
          this.updatingIds.update((ids) => {
            const updated = new Set(ids);
            updated.delete(loan.id);
            return updated;
          }),
        ),
      )
      .subscribe({
        next: () => {
          this.successStatus.set(status);
          this.successMessage.set(`La solicitud #${loan.id} fue ${status === 'APPROVED' ? 'aprobada' : 'rechazada'}.`);
          this.loadLoans();
        },
        error: (error: unknown) => this.errorMessage.set(this.describeError(error)),
      });
  }

  logout(): void {
    this.auth.logout();
    void this.router.navigateByUrl('/login');
  }

  private describeError(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.status === 0) {
      return 'No se pudo conectar con el servidor. Inténtalo más tarde.';
    }
    if (error instanceof HttpErrorResponse && error.status === 403) {
      return 'Tu cuenta no tiene permisos de administrador para esta acción.';
    }
    return 'No se pudieron cargar o actualizar las solicitudes. Actualiza la página e inténtalo de nuevo.';
  }
}
