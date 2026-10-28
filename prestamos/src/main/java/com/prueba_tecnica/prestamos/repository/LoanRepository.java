package com.prueba_tecnica.prestamos.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prueba_tecnica.prestamos.model.Loan;
import com.prueba_tecnica.prestamos.model.LoanStatus;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    // para que un usuario pueda ver solo sus prestamos
    Page<Loan> findByUserId(Long userId, Pageable pageable);

    // para filtrado
    Page<Loan> findByStatus(LoanStatus status, Pageable pageable);
}