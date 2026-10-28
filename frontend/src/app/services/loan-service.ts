import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment-development';
import { Loan, LoanStatus } from '../models/loan.model';
import { PageResponse } from '../models/page-response.model';

export interface LoanRequest {
  amount: number;
  term: number;
}

@Injectable({ providedIn: 'root' })
export class LoanService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/loans`;

  getMyLoans(page = 0, size = 10): Observable<PageResponse<Loan>> {
    return this.http.get<PageResponse<Loan>>(this.apiUrl, {
      params: new HttpParams().set('page', page).set('size', size),
    });
  }

  getAllLoans(page = 0, size = 10): Observable<PageResponse<Loan>> {
    return this.http.get<PageResponse<Loan>>(`${this.apiUrl}/all`, {
      params: new HttpParams().set('page', page).set('size', size),
    });
  }

  requestLoan(request: LoanRequest): Observable<unknown> {
    return this.http.post(this.apiUrl, request);
  }

  updateStatus(id: number, status: Extract<LoanStatus, 'APPROVED' | 'REJECTED'>): Observable<unknown> {
    return this.http.put(`${this.apiUrl}/${id}/status`, { status });
  }
}
