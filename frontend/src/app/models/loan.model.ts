export type LoanStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface Loan {
  id: number;
  amount: number;
  term: number;
  status: LoanStatus;
  startDate: string;
  user?: { email: string };
}