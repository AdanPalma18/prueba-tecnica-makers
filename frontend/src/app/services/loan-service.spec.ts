import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';

import { LoanService } from './loan-service';

describe('LoanService', () => {
  let service: LoanService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(LoanService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('requests the selected server-side page for the current user', () => {
    service.getMyLoans(2, 5).subscribe((page) => {
      expect(page.number).toBe(2);
      expect(page.totalElements).toBe(11);
      expect(page.content).toHaveLength(1);
    });

    const request = httpTesting.expectOne(
      (candidate) =>
        candidate.url === 'http://localhost:8080/api/loans' &&
        candidate.params.get('page') === '2' &&
        candidate.params.get('size') === '5',
    );
    expect(request.request.method).toBe('GET');
    request.flush({
      content: [{ id: 11, amount: 500, term: 6, status: 'PENDING', startDate: '2026-10-28' }],
      totalElements: 11,
      totalPages: 3,
      number: 2,
      size: 5,
      numberOfElements: 1,
      first: false,
      last: true,
      empty: false,
    });
  });
});
