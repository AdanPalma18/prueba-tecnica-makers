import { HttpErrorResponse } from '@angular/common/http';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { Loan, LoanStatus } from '../../models/loan.model';
import { PageResponse } from '../../models/page-response.model';
import { AuthService } from '../../services/auth-service';
import { LoanService } from '../../services/loan-service';

@Component({
  selector: 'app-user-dashboard',
  imports: [ReactiveFormsModule, CurrencyPipe, DatePipe, RouterLink],
  templateUrl: './user-dashboard.html',
  styleUrl: './user-dashboard.css',
})
export class UserDashboard implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly loanService = inject(LoanService);
  private readonly router = inject(Router);

  readonly auth = inject(AuthService);
  readonly loans = signal<Loan[]>([]);
  readonly loadingLoans = signal(true);
  readonly submitting = signal(false);
  readonly page = signal(0);
  readonly pageSize = signal(5);
  readonly totalElements = signal(0);
  readonly totalPages = signal(0);
  readonly firstItem = signal(0);
  readonly lastItem = signal(0);
  readonly message = signal('');
  readonly errorMessage = signal('');
  readonly form = this.formBuilder.nonNullable.group({
    amount: [null as number | null, [Validators.required, Validators.min(100)]],
    term: [null as number | null, [Validators.required, Validators.min(1), Validators.max(360)]],
  });

  ngOnInit(): void {
    this.loadLoans();
  }

  submitLoan(): void {
    this.message.set('');
    this.errorMessage.set('');
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    this.loanService
      .requestLoan({
        amount: Number(this.form.controls.amount.value),
        term: Number(this.form.controls.term.value),
      })
      .pipe(finalize(() => this.submitting.set(false)))
      .subscribe({
        next: () => {
          this.message.set('Tu solicitud fue enviada. Te avisaremos cuando cambie su estado.');
          this.form.reset({ amount: null, term: null });
          this.loadLoans(0);
        },
        error: (error: unknown) => this.errorMessage.set(this.describeError(error)),
      });
  }

  loadLoans(pageNumber = this.page()): void {
    this.loadingLoans.set(true);
    this.errorMessage.set('');
    this.loanService
      .getMyLoans(pageNumber, this.pageSize())
      .pipe(finalize(() => this.loadingLoans.set(false)))
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

  statusLabel(status: LoanStatus): string {
    return { PENDING: 'En revisión', APPROVED: 'Aprobado', REJECTED: 'Rechazado' }[status];
  }

  logout(): void {
    this.auth.logout();
    void this.router.navigateByUrl('/login');
  }

  private describeError(error: unknown): string {
    if (error instanceof HttpErrorResponse && error.status === 0) {
      return 'No se pudo conectar con el servidor. Inténtalo más tarde.';
    }
    if (error instanceof HttpErrorResponse && error.status === 400) {
      return 'Revisa el monto y el plazo e inténtalo de nuevo.';
    }
    return 'Ocurrió un error al procesar la solicitud. Inténtalo de nuevo.';
  }
}
